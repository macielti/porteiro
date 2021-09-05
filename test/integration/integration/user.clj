(ns integration.user
  (:use [clojure pprint])
  (:require [clojure.test :refer :all]
            [fixtures.user]
            [integration.aux.http :as http]
            [matcher-combinators.test :refer [match?]]
            [com.stuartsierra.component :as component]
            [microservice-user-management.components :as components]))

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

