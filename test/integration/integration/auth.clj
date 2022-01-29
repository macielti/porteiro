(ns integration.auth
  (:require [clojure.test :refer :all]
            [integration.aux.http :as http]
            [matcher-combinators.test :refer [match?]]
            [com.stuartsierra.component :as component]
            [common-clj.component.helper.core :as component.helper]
            [common-clj.component.kafka.producer :as kafka.producer]
            [porteiro.components :as components]
            [fixtures.user]))

(deftest auth-test
  (let [{{kafka-producer :producer} :producer :as system} (component/start components/system-test)
        service-fn (-> (component.helper/get-component-content :service system)
                       :io.pedestal.http/service-fn)
        _          (http/create-user! fixtures.user/user
                                      service-fn)]

    (testing "that users can be authenticated"
      (is (match? {:status 200
                   :body   {:token string?}}
                  (http/authenticate-user! fixtures.user/user-auth
                                           service-fn))))

    (testing "that successful authentication notifications the user"
      (is (match? [{:topic :notification
                    :value {:email   (:email fixtures.user/user)
                            :title   "Authentication Confirmation"
                            :content string?}}]
                  (kafka.producer/mock-produced-messages kafka-producer))))

    (testing "that users can't be authenticated with wrong credentials"
      (is (match? {:status 403
                   :body   {:cause "Wrong username or/and password"}}
                  (http/authenticate-user! (assoc fixtures.user/user-auth :password "wrong-password")
                                           service-fn))))

    (testing "that invalid credential schema input return a nice and readable error"
      (is (match? {:status 422
                   :body   {:cause {:username "missing-required-key"}}}
                  (http/authenticate-user! {:password "wrong-password"}
                                           service-fn))))

    (component/stop system)))
