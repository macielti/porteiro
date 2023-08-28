(ns integration.customer
  (:require [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [com.stuartsierra.component :as component]
            [clj-uuid]
            [common-clj.component.helper.core :as component.helper]
            [integration.aux.http :as http]
            [fixtures.user]
            [next.jdbc :as jdbc]
            [porteiro.components :as components]
            [schema.test :as s]
            [porteiro.db.datalevin.user :as database.user])
  (:import (java.util UUID)))

(s/deftest create-customer-test
  (let [system (component/start components/system-test)
        service-fn (-> (component.helper/get-component-content :service system)
                       :io.pedestal.http/service-fn)
        database-connection (component.helper/get-component-content :postgresql system)]

    (jdbc/execute-one! database-connection
                       ["TRUNCATE contact"])
    (jdbc/execute-one! database-connection
                       ["TRUNCATE customer"])

    (testing "that users can be created"
      (is (match? {:status 201
                   :body   {:customer {:id       string?
                                       :username "ednaldo-pereira"
                                       :roles    []}
                            :contact  {:id         string?
                                       :user-id    string?
                                       :type       "EMAIL",
                                       :status     "ACTIVE",
                                       :email      "example@example.com",
                                       :created-at string?}}}
                  (http/create-customer! fixtures.user/wire-customer-creation
                                         service-fn))))

    (testing "that username must be unique"
      (is (= {:status 409
              :body   {:detail  "username already in use by other customer"
                       :error   "not-unique"
                       :message "Username already in use"}}
             (http/create-customer! fixtures.user/wire-customer-creation
                                    service-fn))))

    (testing "that email must be unique"
      (is (= {:status 409
              :body   {:detail  "Email already in use as contact by another user"
                       :error   "not-unique"
                       :message "Email already in use"}}
             (http/create-customer! (assoc-in fixtures.user/wire-customer-creation [:customer :username] "random-username")
                                    service-fn))))

    (testing "request body must respect the schema"
      (is (= {:status 422
              :body   {:detail  {:customer {:username "missing-required-key"}}
                       :error   "invalid-schema-in"
                       :message "The system detected that the received data is invalid"}}
             (http/create-customer! (update fixtures.user/wire-customer-creation :customer dissoc :username)
                                    service-fn))))

    (component/stop system)))

(s/deftest contact-entity
  (let [system (component/start components/system-test)
        service-fn (-> (component.helper/get-component-content :service system)
                       :io.pedestal.http/service-fn)
        database-connection (component.helper/get-component-content :postgresql system)
        _ (do (jdbc/execute-one! database-connection
                                 ["TRUNCATE contact"])
              (jdbc/execute-one! database-connection
                                 ["TRUNCATE customer"]))
        _ (http/create-customer! fixtures.user/wire-customer-creation service-fn)
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
  (let [system (component/start components/system-test)
        service-fn (-> (component.helper/get-component-content :service system)
                       :io.pedestal.http/service-fn)
        _ (http/create-customer! fixtures.user/wire-customer-creation service-fn)
        {{:keys [token]} :body} (http/authenticate-user! fixtures.user/user-auth service-fn)]

    (testing "that we can update password"
      (is (match? {:status 204
                   :body   nil?}
                  (http/update-password! fixtures.user/password-update token service-fn))))

    (testing "that i can't update a password if the old one is incorrect"
      (is (match? {:status 403
                   :body   {:error   "invalid-credentials"
                            :message "The old password you have entered is incorrect"
                            :detail  "Incorrect old password"}}
                  (http/update-password! (assoc fixtures.user/password-update :oldPassword "wrong-old-password") token service-fn))))

    (testing "should return a nice and readable response in case of wrong input"
      (is (match? {:status 422
                   :body   {:error   "invalid-schema-in"
                            :message "The system detected that the received data is invalid"
                            :detail  {:oldPassword "missing-required-key"}}}
                  (http/update-password! (dissoc fixtures.user/password-update :oldPassword) token service-fn))))

    ;TODO: This could be separated in to an isolated test for the auth interceptor
    ;but for now i think it is ok
    (testing "shouldn't be able to change update a password with a invalid jwt token"
      (is (match? {:status 422
                   :body   {:error   "invalid-jwt"
                            :message "Invalid JWT"
                            :detail  "Invalid JWT"}}
                  (http/update-password! fixtures.user/password-update "invalid-jwt-token" service-fn))))

    (component/stop system)))


(s/deftest add-role-to-user
  (testing "that only authenticated users that have the admin role can call this endpoint"
    (let [system (component/start components/system-test)
          service-fn (-> (component.helper/get-component-content :service system) :io.pedestal.http/service-fn)
          datalevin-connection (component.helper/get-component-content :datalevin system)
          {wire-user-id :id} (-> (http/create-customer! fixtures.user/wire-customer-creation service-fn) :body :user)
          {wire-admin-user-id :id} (-> (http/create-customer! fixtures.user/wire-admin-user-creation service-fn) :body :user)
          _ (database.user/add-role! (UUID/fromString wire-admin-user-id) :admin datalevin-connection)
          {{:keys [token]} :body} (http/authenticate-user! fixtures.user/admin-user-auth service-fn)]

      (is (match? {:status 200
                   :body   {:user {:id       wire-user-id
                                   :username "ednaldo-pereira"
                                   :roles    ["ADMIN"]}}}
                  (http/add-role! token wire-user-id "ADMIN" service-fn)))

      (component/stop system)))

  (testing "if you don't have the admin role you can't add role to others"
    (let [system (component/start components/system-test)
          service-fn (-> (component.helper/get-component-content :service system) :io.pedestal.http/service-fn)
          datomic-connection (-> (component.helper/get-component-content :datomic system) :connection)
          consumer (component.helper/get-component-content :consumer system)
          {wire-user-id :id} (-> (http/create-customer! fixtures.user/user service-fn) :body :user)
          {wire-admin-user-id :id} (-> (http/create-customer! fixtures.user/wire-admin-user-creation service-fn) :body :user)
          {{:keys [token]} :body} (http/authenticate-user! fixtures.user/admin-user-auth service-fn)]

      (is (match? {:status 403
                   :body   {:error   "insufficient-roles"
                            :message "Insufficient privileges/roles/permission"
                            :detail  "Insufficient privileges/roles/permission"}}
                  (http/add-role! token wire-user-id "ADMIN" service-fn)))

      (component/stop system))))
