(ns porteiro.adapters.password-reset-test
  (:require [clojure.test :refer :all]
            [porteiro.adapters.password-reset :as adapters.password-reset]
            [fixtures.password-reset]
            [schema.test :as s]))

(s/deftest postgresql->internal-test
  (testing "that we can internalize a wire entity from postgresql"
    (is (= fixtures.password-reset/password-reset
           (adapters.password-reset/postgresql->internal fixtures.password-reset/password-reset-postgresql)))))
