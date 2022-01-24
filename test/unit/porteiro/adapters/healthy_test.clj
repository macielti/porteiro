(ns microservice-user-management.adapters.healthy-test
  (:require [clojure.test :refer :all]
            [schema.test :as s]
            [microservice-user-management.adapters.healthy :as adapters.healthy]))

(use-fixtures :once s/validate-schemas)

(s/deftest healthy-check-result->status-code-test
  (testing "that we can convert the healthy check result to an success status code"
    (is (= 200
           (adapters.healthy/healthy-check-result->status-code {:is-healthy true
                                                                :components [{:component  :datomic
                                                                              :is-healthy true}
                                                                             {:component  :config
                                                                              :is-healthy true}]}))))
  (testing "that we can convert the healthy check result to an failure status code"
    (is (= 503
           (adapters.healthy/healthy-check-result->status-code {:is-healthy false
                                                                :components [{:component  :datomic
                                                                              :is-healthy false}
                                                                             {:component  :config
                                                                              :is-healthy true}]})))))

(s/deftest component-healthy-check-result->wire-test
  (testing "that we can externalize healthy check component results"
    (is (= {:component "datomic"
            :isHealthy true}
           (adapters.healthy/component-healthy-check-result->wire {:component  :datomic
                                                                   :is-healthy true})))))

(s/deftest ->wire-test
  (testing "that we can externalize healthy check results"
    (is (= {:isHealthy  true
            :components [{:component "datomic"
                          :isHealthy true}
                         {:component "config"
                          :isHealthy true}]}
           (adapters.healthy/->wire {:is-healthy true
                                     :components [{:component  :datomic
                                                   :is-healthy true}
                                                  {:component  :config
                                                   :is-healthy true}]})))))
