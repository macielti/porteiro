(ns microservice-user-management.adapters.auth-test
  (:require [clojure.test :refer :all]
            [microservice-user-management.adapters.auth :as adapters.auth]
            [schema.test :as s])
  (:import (clojure.lang ExceptionInfo)))

(use-fixtures :once s/validate-schemas)

(s/deftest wire->internal-test
  (testing "that we can verify the user schema input from outside"
    (is (= {:username "ednaldo-pereira"
            :password "a-very-strong-password"}
           (adapters.auth/wire->internal {:username "ednaldo-pereira"
                                          :password "a-very-strong-password"}))))
  (testing "that a invalid input will throws a exception"
    (is (thrown? ExceptionInfo (adapters.auth/wire->internal {:username "ednaldo-pereira"
                                                              :name     "Ednaldo Pereira"})))))
