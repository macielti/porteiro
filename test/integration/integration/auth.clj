(ns integration.auth
  (:require [clojure.test :refer :all]
            [fixtures.contact]
            [fixtures.customer]
            [integrant.core :as ig]
            [integration.aux.http :as http]
            [matcher-combinators.matchers :as m]
            [matcher-combinators.test :refer [match?]]
            [porteiro.v2.components :as v2.components]
            [schema.test :as s]))

(s/deftest auth-test
  (let [system (ig/init v2.components/config-test)
        producer (-> system :common-clj.integrant-components.sqs-producer/sqs-producer)
        service-fn (-> system :common-clj.integrant-components.service/service :io.pedestal.http/service-fn)]

    (testing "that users can be created"
      (is (match? {:status 201
                   :body   {:contact  {:id          string?
                                       :customer-id string?
                                       :email       "test@example.com"
                                       :type        "EMAIL"
                                       :status      "ACTIVE"}
                            :customer {:id       string?
                                       :roles    []
                                       :username "manoel-gomes"}}}
                  (http/create-customer! fixtures.customer/wire-customer-creation
                                         service-fn))))

    (Thread/sleep 5000)

    (testing "that users can be authenticated"
      (is (match? {:status 200
                   :body   {:token string?}}
                  (http/authenticate-user! fixtures.customer/wire-customer-auth
                                           service-fn))))

    (testing "that successful authentication notifications the user"
      (is (match? (m/in-any-order [{:queue   "notification"
                                    :payload {:email   fixtures.contact/email
                                              :title   "Authentication Confirmation"
                                              :content string?}}])
                  @(:produced-messages producer))))

    (testing "that users can't be authenticated with wrong credentials"
      (is (match? {:status 403
                   :body   {:error   "invalid-credentials"
                            :message "Wrong username or/and password"
                            :detail  "Customer is trying to login using invalid credentials"}}
                  (http/authenticate-user! (assoc fixtures.customer/wire-customer-auth :password "wrong-password")
                                           service-fn))))

    (testing "that invalid credential schema input return a nice and readable error"
      (is (match? {:status 422
                   :body   {:error   "invalid-schema-in"
                            :message "The system detected that the received data is invalid"
                            :detail  {:username "missing-required-key"}}}
                  (http/authenticate-user! {:password "wrong-password"}
                                           service-fn))))

    (ig/halt! system)))
