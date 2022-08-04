(ns integration.reset-password
  (:require [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [com.stuartsierra.component :as component]
            [common-clj.component.helper.core :as component.helper]
            [common-clj.component.kafka.producer :as kafka.producer]
            [integration.aux.http :as http]
            [porteiro.components :as components]
            [fixtures.user]))

(deftest reset-password-test
  (testing "request body must respect the schema"
    (let [system (component/start components/system-test)
          service-fn (-> (component.helper/get-component-content :service system)
                         :io.pedestal.http/service-fn)]

      (is (match? {:status 422
                   :body   {:error   "invalid-schema-in"
                            :message "The system detected that the received data is invalid",
                            :detail  {:email    "missing-required-key"
                                      :nonSense "disallowed-key"}}}
                  (http/request-reset-password! {:nonSense "hi lorena"}
                                                service-fn)))

      (component/stop system)))
  (testing "that reset password request will produce a message when existent"
    (let [{{kafka-producer :producer} :producer :as system} (component/start components/system-test)
          service-fn (-> (component.helper/get-component-content :service system) :io.pedestal.http/service-fn)
          {{:keys [email]} :user} (:body (http/create-user! fixtures.user/user service-fn))]

      (Thread/sleep 5000)

      (is (match? {:status 202
                   :body   {:message
                            "If the email is correct, you should receive a password reset link soon"}}
                  (http/request-reset-password! {:email email} service-fn)))

      (is (match? [{:topic :notification
                    :data  {:payload {:email   email
                                      :title   "Password Reset Solicitation"
                                      :content string?}}}]
                  (filter #(= (:topic %) :notification)
                          (kafka.producer/produced-messages kafka-producer))))

      (component/stop system)))

  (testing "that trying to reset password with a nonexistent email will not produce any message"
    (let [{{kafka-producer :producer} :producer :as system} (component/start components/system-test)
          service-fn (-> (component.helper/get-component-content :service system) :io.pedestal.http/service-fn)]

      (is (match? {:status 202
                   :body   {:message
                            "If the email is correct, you should receive a password reset link soon"}}
                  (http/request-reset-password! {:email "nonexistent@example.com"} service-fn)))

      (Thread/sleep 5000)

      (is (match? []
                  (kafka.producer/produced-messages kafka-producer)))

      (component/stop system))))

(deftest execute-password-reset-test
  (testing "request body must respect the schema"
    (let [system (component/start components/system-test)
          service-fn (-> (component.helper/get-component-content :service system) :io.pedestal.http/service-fn)]

      (is (match? {:status 422
                   :body   nil?}
                  (http/reset-password! {:newPassword (:newPassword fixtures.user/password-update)}
                                        service-fn)))

      (component/stop system)))

  (testing "that we can consolidate the reset password solicitation"
    (let [{{kafka-producer :producer} :producer :as system} (component/start components/system-test)
          service-fn (-> (component.helper/get-component-content :service system) :io.pedestal.http/service-fn)
          {{:keys [email]} :user} (:body (http/create-user! fixtures.user/user service-fn))
          _ (Thread/sleep 5000)
          _ (http/request-reset-password! {:email email} service-fn)
          {{:keys [payload]} :data} (first (filter #(= (:topic %) :notification)
                                                                (kafka.producer/produced-messages kafka-producer)))]

      (is (match? {:status 204
                   :body   nil?}
                  (http/reset-password! {:token       (:password-reset-id payload)
                                         :newPassword (:newPassword fixtures.user/password-update)}
                                        service-fn)))

      (is (match? {:status 200
                   :body   {:token string?}}
                  (http/authenticate-user! (assoc fixtures.user/user-auth :password (:newPassword fixtures.user/password-update))
                                           service-fn)))

      (is (match? {:status 403
                   :body   {:error   "invalid-credentials"
                            :message "Wrong username or/and password"
                            :detail  "user is trying to login using invalid credentials"}}
                  (http/authenticate-user! fixtures.user/user-auth
                                           service-fn)))

      (component/stop system)))

  (testing "that we can't utilize the same token to consolidate password reset a second time"
    (let [{{kafka-producer :producer} :producer :as system} (component/start components/system-test)
          service-fn (-> (component.helper/get-component-content :service system) :io.pedestal.http/service-fn)
          consumer (component.helper/get-component-content :consumer system)
          {{:keys [email]} :user} (:body (http/create-user! fixtures.user/user service-fn))
          _ (Thread/sleep 5000)
          _ (http/request-reset-password! {:email email} service-fn)
          {{:keys [payload]} :data} (first (filter #(= (:topic %) :notification)
                                                                (kafka.producer/produced-messages kafka-producer)))
          _ (http/reset-password! {:token       (:password-reset-id payload)
                                   :newPassword (:newPassword fixtures.user/password-update)}
                                  service-fn)]

      (is (match? {:status 401
                   :body   nil?}
                  (http/reset-password! {:token       (:password-reset-id payload)
                                         :newPassword (:newPassword fixtures.user/password-update)}
                                        service-fn)))

      (component/stop system))))
