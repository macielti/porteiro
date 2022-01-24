(ns integration.healthy
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [integration.aux.http :as http]
            [porteiro.components :as components]))

(deftest create-user-test
  (let [system     (components/start-system!)
        service-fn (-> system :server :server :io.pedestal.http/service-fn)]

    (testing "that users can be created"
      (is (= {:status 200
              :body   {:isHealthy  true
                       :components [{:component "config"
                                     :isHealthy true}
                                    {:component "datomic"
                                     :isHealthy true}]}}
             (http/healthy-check service-fn))))
    (component/stop system)))
