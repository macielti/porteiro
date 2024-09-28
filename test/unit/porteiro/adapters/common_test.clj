(ns porteiro.adapters.common-test
  (:require [clojure.test :refer :all]
            [porteiro.adapters.common :as adapters.common]
            [schema.test :as s]))

(s/deftest str->keyword-kebabed-case-test
  (testing "that loweCamelCase strings can be converted to kebab case"
    (is (= :datomic-uri
           (adapters.common/str->keyword-kebab-case "datomicUri")))))
