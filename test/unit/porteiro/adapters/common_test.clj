(ns porteiro.adapters.common-test
  (:require [clojure.test :refer :all]
            [schema.test :as s]
            [porteiro.adapters.common :as adapters.common]))

(use-fixtures :once s/validate-schemas)

(s/deftest str->keyword-kebabed-case-test
           (testing "that loweCamelCase strings can be converted to kebab case"
             (is (= :datomic-uri
                    (adapters.common/str->keyword-kebab-case "datomicUri")))))
