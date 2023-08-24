(ns porteiro.adapters.customer-test
  (:require [clojure.test :refer :all]
            [porteiro.adapters.customer :as adapters.customer]
            [fixtures.customer]
            [schema.test :as s]))

(s/deftest postgresql->internal-test
  (testing "that we can adapt a postgresql customer entity to internal model"
    (is (= (dissoc fixtures.customer/customer :customer/roles)
           (adapters.customer/postgresql->internal fixtures.customer/postgresql-customer)))))
