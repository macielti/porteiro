(ns integration.healthy
  (:require [clojure.test :refer :all]
            [common-clj.component.helper.core :as component.helper]
            [com.stuartsierra.component :as component]
            [integration.aux.http :as http]
            [porteiro.components :as components]))

(deftest create-user-test
  (let [system     (components/start-system!)
        service-fn (-> (component.helper/get-component-content :service system)
                       :io.pedestal.http/service-fn)]

    (testing "that users can be created"
      (is (= {:status 200
              :body   {:isHealth   true
                       :components [{:component "config"
                                     :isHealth  true}
                                    {:component "datomic"
                                     :isHealth  true}]}}
             (http/health-check service-fn))))
    (component/stop system)))
