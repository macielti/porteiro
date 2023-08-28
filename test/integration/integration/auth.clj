(ns integration.auth
  (:require [clojure.test :refer :all]
            [integration.aux.http :as http]
            [matcher-combinators.test :refer [match?]]
            [matcher-combinators.matchers :as m]
            [com.stuartsierra.component :as component]
            [common-clj.component.helper.core :as component.helper]
            [porteiro.components :as components]
            [fixtures.user]
            [next.jdbc :as jdbc]
            [schema.test :as s]))

(s/deftest auth-test
  (let [system (component/start components/system-test)
        producer (component.helper/get-component-content :rabbitmq-producer system)
        service-fn (-> (component.helper/get-component-content :service system)
                       :io.pedestal.http/service-fn)
        database-connection (component.helper/get-component-content :postgresql system)
        _ (do (jdbc/execute-one! database-connection
                                 ["TRUNCATE contact"])
              (jdbc/execute-one! database-connection
                                 ["TRUNCATE customer"]))
        _ (http/create-customer! fixtures.user/wire-customer-creation
                                 service-fn)]

    (Thread/sleep 5000)

    (testing "that users can be authenticated"
      (is (match? {:status 200
                   :body   {:token string?}}
                  (http/authenticate-user! fixtures.user/user-auth
                                           service-fn))))

    (testing "that successful authentication notifications the user"
      (is (match? (m/in-any-order [{:topic   :notification
                                    :payload {:email   (:email fixtures.user/user)
                                              :title   "Authentication Confirmation"
                                              :content string?}}])
                  (filter #(= (:topic %) :notification)
                          @(:produced-messages producer)))))

    (testing "that users can't be authenticated with wrong credentials"
      (is (match? {:status 403
                   :body   {:error   "invalid-credentials"
                            :message "Wrong username or/and password"
                            :detail  "Customer is trying to login using invalid credentials"}}
                  (http/authenticate-user! (assoc fixtures.user/user-auth :password "wrong-password")
                                           service-fn))))

    (testing "that invalid credential schema input return a nice and readable error"
      (is (match? {:status 422
                   :body   {:error   "invalid-schema-in"
                            :message "The system detected that the received data is invalid"
                            :detail  {:username "missing-required-key"}}}
                  (http/authenticate-user! {:password "wrong-password"}
                                           service-fn))))

    (component/stop system)))
