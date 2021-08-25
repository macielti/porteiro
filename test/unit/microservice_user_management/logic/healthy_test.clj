(ns microservice-user-management.logic.healthy-test
  (:require [clojure.test :refer :all]
            [schema.test :as s]
            [microservice-user-management.logic.healthy :as logic.healthy]))


(s/deftest system-healthy?-test
  (testing "that we can verify the healthy state of the entire system"
    (is (= true
           (logic.healthy/system-healthy? {:components [{:component  :datomic
                                                         :is-healthy true}
                                                        {:component  :config
                                                         :is-healthy true}]}))))
  (testing "that we can verify the healthy state of the entire system in case of failure"
    (is (= false
           (logic.healthy/system-healthy? {:components [{:component  :datomic
                                                         :is-healthy false}
                                                        {:component  :config
                                                         :is-healthy true}]}))))
  (testing "that we can verify the healthy state of the entire system in case of entire system is down"
    (is (= false
           (logic.healthy/system-healthy? {:components [{:component  :datomic
                                                         :is-healthy false}
                                                        {:component  :config
                                                         :is-healthy false}]})))))
