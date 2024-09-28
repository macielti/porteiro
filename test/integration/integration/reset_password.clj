(ns integration.reset-password
  (:require [clojure.test :refer :all]
            [fixtures.customer]
            [integrant.core :as ig]
            [integration.aux.http :as http]
            [matcher-combinators.test :refer [match?]]
            [porteiro.v2.components :as v2.components]))

(deftest reset-password-test
  (testing "request body must respect the schema"
    (let [system (ig/init v2.components/config-test)
          service-fn (get-in system [:common-clj.integrant-components.service/service :io.pedestal.http/service-fn])]

      (is (match? {:status 422
                   :body   {:error   "invalid-schema-in"
                            :message "The system detected that the received data is invalid",
                            :detail  {:email    "missing-required-key"
                                      :nonSense "disallowed-key"}}}
                  (http/request-reset-password! {:nonSense "hi lorena"}
                                                service-fn)))

      (ig/halt! system)))

  (testing "that reset password request will produce a message when existent"
    (let [system (ig/init v2.components/config-test)
          producer (-> system :common-clj.integrant-components.sqs-producer/sqs-producer)
          service-fn (get-in system [:common-clj.integrant-components.service/service :io.pedestal.http/service-fn])
          {{:keys [email]} :contact} (:body (http/create-customer! fixtures.customer/wire-customer-creation service-fn))]

      (Thread/sleep 5000)

      (is (match? {:status 202
                   :body   {:message
                            "If the email is correct, you should receive a password reset link soon"}}
                  (http/request-reset-password! {:email email} service-fn)))

      (is (match? [{:queue   "notification"
                    :payload {:email   email
                              :title   "Password Reset Solicitation"
                              :content string?}}]
                  @(:produced-messages producer)))

      (ig/halt! system)))

  (testing "that trying to reset password with a nonexistent email will not produce any message"
    (let [system (ig/init v2.components/config-test)
          producer (-> system :common-clj.integrant-components.sqs-producer/sqs-producer)
          service-fn (get-in system [:common-clj.integrant-components.service/service :io.pedestal.http/service-fn])]

      (is (match? {:status 202
                   :body   {:message
                            "If the email is correct, you should receive a password reset link soon"}}
                  (http/request-reset-password! {:email "nonexistent@example.com"} service-fn)))

      (Thread/sleep 5000)

      (is (match? []
                  @(:produced-messages producer)))

      (ig/halt! system))))

(deftest execute-password-reset-test
  (testing "request body must respect the schema"
    (let [system (ig/init v2.components/config-test)
          service-fn (get-in system [:common-clj.integrant-components.service/service :io.pedestal.http/service-fn])]

      (is (match? {:status 422
                   :body   nil?}
                  (http/reset-password! {:newPassword (:newPassword fixtures.customer/password-update)}
                                        service-fn)))

      (ig/halt! system)))

  (testing "that we can consolidate the reset password solicitation"
    (let [system (ig/init v2.components/config-test)
          producer (-> system :common-clj.integrant-components.sqs-producer/sqs-producer)
          service-fn (get-in system [:common-clj.integrant-components.service/service :io.pedestal.http/service-fn])
          {{:keys [email]} :contact} (:body (http/create-customer! fixtures.customer/wire-customer-creation service-fn))
          _ (Thread/sleep 5000)
          _ (http/request-reset-password! {:email email} service-fn)
          {:keys [payload]} (first (filter #(= (:queue %) "notification")
                                           @(:produced-messages producer)))]

      (is (match? {:status 204
                   :body   nil?}
                  (http/reset-password! {:token       (:password-reset-id payload)
                                         :newPassword (:newPassword fixtures.customer/password-update)}
                                        service-fn)))

      (is (match? {:status 200
                   :body   {:token string?}}
                  (http/authenticate-user! (assoc fixtures.customer/wire-customer-auth :password (:newPassword fixtures.customer/password-update))
                                           service-fn)))

      (is (match? {:status 403
                   :body   {:error   "invalid-credentials"
                            :message "Wrong username or/and password"
                            :detail  "Customer is trying to login using invalid credentials"}}
                  (http/authenticate-user! fixtures.customer/wire-customer-auth
                                           service-fn)))

      (ig/halt! system)))

  (testing "that we can't utilize the same token to consolidate password reset a second time"
    (let [system (ig/init v2.components/config-test)
          service-fn (get-in system [:common-clj.integrant-components.service/service :io.pedestal.http/service-fn])
          producer (-> system :common-clj.integrant-components.sqs-producer/sqs-producer)
          {{:keys [email]} :contact} (:body (http/create-customer! fixtures.customer/wire-customer-creation service-fn))
          _ (Thread/sleep 5000)
          _ (http/request-reset-password! {:email email} service-fn)
          {:keys [payload]} (first (filter #(= (:queue %) "notification")
                                           @(:produced-messages producer)))
          _ (http/reset-password! {:token       (:password-reset-id payload)
                                   :newPassword (:newPassword fixtures.customer/password-update)}
                                  service-fn)]

      (is (match? {:status 401
                   :body   nil?}
                  (http/reset-password! {:token       (:password-reset-id payload)
                                         :newPassword (:newPassword fixtures.customer/password-update)}
                                        service-fn)))

      (ig/halt! system))))
