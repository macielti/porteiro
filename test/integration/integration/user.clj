(ns integration.user
  (:require [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [com.stuartsierra.component :as component]
            [clj-uuid]
            [common-clj.component.helper.core :as component.helper]
            [integration.aux.http :as http]
            [fixtures.user]
            [porteiro.components :as components]))

(deftest create-user-test
  (let [system     (component/start components/system-test)
        service-fn (-> (component.helper/get-component-content :service system)
                       :io.pedestal.http/service-fn)]

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

(deftest contact-entity
  (let [system     (component/start components/system-test)
        service-fn (-> (component.helper/get-component-content :service system)
                       :io.pedestal.http/service-fn)
        _          (http/create-user! fixtures.user/user service-fn)
        {{:keys [token]} :body} (http/authenticate-user! fixtures.user/user-auth service-fn)]

    (testing "that the contact entity is created"
      (is (match? {:status 200
                   :body   [{:contact/id         clj-uuid/uuid-string?
                             :contact/user-id    clj-uuid/uuid-string?
                             :contact/type       "email"
                             :contact/created-at string?
                             :contact/email      "example@example.com"}]}
                  (http/fetch-contacts token service-fn))))

    (component/stop system)))

(deftest update-password-test
  (let [system     (component/start components/system-test)
        service-fn (-> (component.helper/get-component-content :service system)
                       :io.pedestal.http/service-fn)
        _          (http/create-user! fixtures.user/user service-fn)
        {{:keys [token]} :body} (http/authenticate-user! fixtures.user/user-auth service-fn)]

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
    (testing "shouldn't be able to change update a password with a invalid jwt token"
      (is (match? {:status 422
                   :body   {:cause "Invalid token"}}
                  (http/update-password! fixtures.user/password-update "invalid-jwt-token" service-fn))))

    (component/stop system)))

