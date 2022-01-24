(ns integration.reset-password
  (:require [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [com.stuartsierra.component :as component]
            [integration.aux.http :as http]
            [fixtures.user]
            [porteiro.components :as components]
            [porteiro.producer :as producer]))

(deftest reset-password-test
  (testing "request body must respect the schema"
    (let [system     (components/start-system!)
          service-fn (-> system :server :server :io.pedestal.http/service-fn)]

      (is (match? {:status 422
                   :body   {:cause
                            {:email    "missing-required-key",
                             :nonSense "disallowed-key"}}}
                  (http/reset-password! {:nonSense "hi lorena"}
                                        service-fn)))

      (component/stop system)))
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
