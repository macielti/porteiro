(ns integration.user
  (:use [clojure pprint])
  (:require [clojure.test :refer :all]
            [fixtures.user]
            [integration.aux.http :as http]
            [matcher-combinators.test :refer [match?]]
            [com.stuartsierra.component :as component]
            [microservice-user-management.components :as components]
            [microservice-user-management.producer :as producer]
            [schema.test :as s]))

(deftest create-user-test
  (let [system     (components/start-system!)
        service-fn (-> system :server :server :io.pedestal.http/service-fn)]

    (testing "that users can be created"
      (is (match? {:status 201
                   :body   {:id       string?
                            :username "ednaldo-pereira"
                            :email    "example@example.com"}}
                  (http/create-user! fixtures.user/user
                                     service-fn))))

    (testing "that username must be unique"
      (is (= {:status 409
              :body   {:cause "username already in use by other user"}}
             (http/create-user! fixtures.user/user
                                service-fn))))

    (testing "request body must respect the schema"
      (is (= {:status 422, :body {:cause {:username "missing-required-key"}}}
             (http/create-user! (dissoc fixtures.user/user :username)
                                service-fn))))

    (component/stop system)))

(deftest update-password-test
  (let [system     (components/start-system!)
        service-fn (-> system :server :server :io.pedestal.http/service-fn)
        _          (http/create-user! fixtures.user/user service-fn)
        {{:keys [token]} :body} (http/auth fixtures.user/user-auth service-fn)]

    (testing "that we can update password"
      (is (match? {:status 204
                   :body   nil?}
                  (http/update-password! fixtures.user/password-update token service-fn))))

    (testing "that i can't update a password if the old one is incorrect"
      (is (match? {:status 403,
                   :body   {:cause "The old password you have entered is incorrect"}}
                  (http/update-password! (assoc fixtures.user/password-update :oldPassword "wrong-old-password") token service-fn))))

    (testing "should return a nice and readable response in case of wrong input"
      (is (match? {:status 422,
                   :body   {:cause {:oldPassword "missing-required-key"}}}
                  (http/update-password! (dissoc fixtures.user/password-update :oldPassword) token service-fn))))

    ;TODO: This could be separated in to an isolated test for the auth interceptor
    ;but for now i think it is ok
    (testing "shouldn't be able to change update a password with a inavalid jwt token"
      (is (match? {:status 422
                   :body   {:cause "Invalid token"}}
                  (http/update-password! fixtures.user/password-update "invalid-jwt-token" service-fn))))

    (component/stop system)))

(deftest reset-password-test
  (testing "that reset password request will produce a message when existent"
    (let [{{kafka-producer :producer} :producer
           :as                        system} (components/start-system!)
          service-fn (-> system :server :server :io.pedestal.http/service-fn)
          {{:keys [email]} :body} (http/create-user! fixtures.user/user service-fn)]

      (is (match? {:status 202
                   :body   {:message
                            "If you email is on our system, you should receive a password reset link soon"}}
                  (http/reset-password! {:email email} service-fn)))

      (is (match? [{:topic :notification
                    :value {:email   email
                            :title   "Password Reset Solicitation"
                            :content string?}}]
                  (producer/mock-produced-messages kafka-producer)))

      (component/stop system)))

  (testing "that trying to reset password with a nonexistent email will not produce any message"
    (let [{{kafka-producer :producer} :producer
           :as                        system} (components/start-system!)
          service-fn (-> system :server :server :io.pedestal.http/service-fn)]

      (is (match? {:status 202
                   :body   {:message
                            "If you email is on our system, you should receive a password reset link soon"}}
                  (http/reset-password! {:email "nonexistent@example.com"} service-fn)))

      (is (match? []
                  (producer/mock-produced-messages kafka-producer)))

      (component/stop system))))

(deftest consolidate-password-reset-test
  (testing "request body must respect the schema"
    (let [system     (components/start-system!)
          service-fn (-> system :server :server :io.pedestal.http/service-fn)]

      (is (match? {:status 422
                   :body   nil?}
                  (http/consolidate-reset-password! {:newPassword (:newPassword fixtures.user/password-update)}
                                                    service-fn)))

      (component/stop system)))

  (testing "that we can consolidate the reset password solicitation"
    (let [{{kafka-producer :producer} :producer
           :as                        system} (components/start-system!)
          service-fn (-> system :server :server :io.pedestal.http/service-fn)
          {{:keys [email]} :body} (http/create-user! fixtures.user/user service-fn)
          _          (http/reset-password! {:email email} service-fn)
          {{:keys [password-reset-id]} :value} (first (producer/mock-produced-messages kafka-producer))]

      (is (match? {:status 204
                   :body   nil?}
                  (http/consolidate-reset-password! {:token       password-reset-id
                                                     :newPassword (:newPassword fixtures.user/password-update)}
                                                    service-fn)))

      (is (match? {:status 200
                   :body   {:token string?}}
                  (http/auth (assoc fixtures.user/user-auth :password (:newPassword fixtures.user/password-update))
                             service-fn)))

      (is (match? {:status 403
                   :body   {:cause "Wrong username or/and password"}}
                  (http/auth fixtures.user/user-auth
                             service-fn)))

      (component/stop system)))

  (testing "that we can't utilize the same token to consolidate password reset a second time"
    (let [{{kafka-producer :producer} :producer
           :as                        system} (components/start-system!)
          service-fn (-> system :server :server :io.pedestal.http/service-fn)
          {{:keys [email]} :body} (http/create-user! fixtures.user/user service-fn)
          _          (http/reset-password! {:email email} service-fn)
          {{:keys [password-reset-id]} :value} (first (producer/mock-produced-messages kafka-producer))
          _          (http/consolidate-reset-password! {:token       password-reset-id
                                                        :newPassword (:newPassword fixtures.user/password-update)}
                                                       service-fn)]

      (is (match? {:status 401
                   :body   nil?}
                  (http/consolidate-reset-password! {:token       password-reset-id
                                                     :newPassword (:newPassword fixtures.user/password-update)}
                                                    service-fn)))

      (component/stop system))))
