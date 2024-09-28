(ns integration.customer
  (:require [clj-uuid]
            [clojure.test :refer :all]
            [fixtures.customer]
            [integrant.core :as ig]
            [integration.aux.http :as http]
            [matcher-combinators.test :refer [match?]]
            [porteiro.v2.components :as v2.components]
            [schema.test :as s]))

(s/deftest create-customer-test
  (let [system (ig/init v2.components/config-test)
        service-fn (-> system :common-clj.integrant-components.service/service :io.pedestal.http/service-fn)]

    (testing "that users can be created"
      (is (match? {:status 201
                   :body   {:customer {:id       string?
                                       :username "manoel-gomes"
                                       :roles    []}
                            :contact  {:id          string?
                                       :customer-id string?
                                       :type        "EMAIL"
                                       :status      "ACTIVE"
                                       :email       "test@example.com"
                                       :created-at  string?}}}
                  (http/create-customer! fixtures.customer/wire-customer-creation
                                         service-fn))))

    (testing "that username must be unique"
      (is (= {:status 409
              :body   {:detail  "username already in use by other customer"
                       :error   "not-unique"
                       :message "Username already in use"}}
             (http/create-customer! fixtures.customer/wire-customer-creation
                                    service-fn))))

    (testing "that email must be unique"
      (is (= {:status 409
              :body   {:detail  "Email already in use as contact by another user"
                       :error   "not-unique"
                       :message "Email already in use"}}
             (http/create-customer! (assoc-in fixtures.customer/wire-customer-creation [:customer :username] "random-username")
                                    service-fn))))

    (testing "request body must respect the schema"
      (is (= {:status 422
              :body   {:detail  {:customer {:username "missing-required-key"}}
                       :error   "invalid-schema-in"
                       :message "The system detected that the received data is invalid"}}
             (http/create-customer! (update fixtures.customer/wire-customer-creation :customer dissoc :username)
                                    service-fn))))

    (ig/halt! system)))

(s/deftest contact-entity
  (let [system (ig/init v2.components/config-test)
        service-fn (-> system :common-clj.integrant-components.service/service :io.pedestal.http/service-fn)
        _ (http/create-customer! fixtures.customer/wire-customer-creation service-fn)
        {{:keys [token]} :body} (http/authenticate-user! fixtures.customer/wire-customer-auth service-fn)]

    (testing "that the contact entity is created"
      (is (match? {:status 200
                   :body   [{:contact/id          clj-uuid/uuid-string?
                             :contact/customer-id clj-uuid/uuid-string?
                             :contact/type        "email"
                             :contact/created-at  string?
                             :contact/email       "test@example.com"}]}
                  (http/fetch-contacts token service-fn))))

    (ig/halt! system)))

(deftest update-password-test
  (let [system (ig/init v2.components/config-test)
        service-fn (-> system :common-clj.integrant-components.service/service :io.pedestal.http/service-fn)
        _ (http/create-customer! fixtures.customer/wire-customer-creation service-fn)
        {{:keys [token]} :body} (http/authenticate-user! fixtures.customer/wire-customer-auth service-fn)]

    (testing "that we can update password"
      (is (match? {:status 204
                   :body   nil?}
                  (http/update-password! fixtures.customer/password-update token service-fn))))

    (testing "that i can't update a password if the old one is incorrect"
      (is (match? {:status 403
                   :body   {:error   "invalid-credentials"
                            :message "The old password you have entered is incorrect"
                            :detail  "Incorrect old password"}}
                  (http/update-password! (assoc fixtures.customer/password-update :oldPassword "wrong-old-password") token service-fn))))

    (testing "should return a nice and readable response in case of wrong input"
      (is (match? {:status 422
                   :body   {:error   "invalid-schema-in"
                            :message "The system detected that the received data is invalid"
                            :detail  {:oldPassword "missing-required-key"}}}
                  (http/update-password! (dissoc fixtures.customer/password-update :oldPassword) token service-fn))))

    ;TODO: This could be separated in to an isolated test for the auth interceptor
    ;but for now i think it is ok
    (testing "shouldn't be able to change update a password with a invalid jwt token"
      (is (match? {:status 422
                   :body   {:error   "invalid-jwt"
                            :message "Invalid JWT"
                            :detail  "Invalid JWT"}}
                  (http/update-password! fixtures.customer/password-update "invalid-jwt-token" service-fn))))

    (ig/halt! system)))

(s/deftest add-role-to-user
  (testing "that only authenticated users that have the admin role can call this endpoint"
    (let [system (ig/init v2.components/config-test)
          service-fn (get-in system [:common-clj.integrant-components.service/service :io.pedestal.http/service-fn])
          {wire-customer-id :id} (get-in (http/create-customer! fixtures.customer/wire-customer-creation service-fn) [:body :customer])
          {{:keys [token]} :body} (http/authenticate-user! fixtures.customer/wire-admin-customer-auth service-fn)]

      (is (match? {:status 200
                   :body   {:customer {:id       wire-customer-id
                                       :username "manoel-gomes"
                                       :roles    ["ADMIN"]}}}
                  (http/add-role! token wire-customer-id "ADMIN" service-fn)))

      (ig/halt! system)))

  (testing "if you don't have the admin role you can't add role to others"
    (let [system (ig/init v2.components/config-test)
          service-fn (get-in system [:common-clj.integrant-components.service/service :io.pedestal.http/service-fn])
          {wire-user-id :id} (-> (http/create-customer! fixtures.customer/wire-customer-creation service-fn) :body :user)
          {{:keys [token]} :body} (http/authenticate-user! fixtures.customer/wire-customer-auth service-fn)]

      (is (match? {:status 403
                   :body   {:error   "insufficient-roles"
                            :message "Insufficient privileges/roles/permission"
                            :detail  "Insufficient privileges/roles/permission"}}
                  (http/add-role! token wire-user-id "ADMIN" service-fn)))

      (ig/halt! system))))
