(ns porteiro.adapters.auth-test
  (:require [clojure.test :refer :all]
            [porteiro.adapters.auth :as adapters.auth]
            [schema.test :as s])
  (:import (clojure.lang ExceptionInfo)))

(s/deftest wire->internal-user-auth-test
  (testing "that we can verify the user schema input from outside"
    (is (= {:customer-auth/password "a-very-strong-password"
            :customer-auth/username "ednaldo-pereira"}
           (adapters.auth/wire->internal-customer-auth {:username "ednaldo-pereira"
                                                        :password     "a-very-strong-password"}))))
  (testing "that a invalid input will throws a exception"
    (is (thrown? ExceptionInfo (adapters.auth/wire->internal-customer-auth {:username "ednaldo-pereira"
                                                                            :name         "Ednaldo Pereira"})))))

(s/deftest token->wire-test
  (testing "that we can externalize token value to wire out"
    (is (= {:token "random-token"}
           (adapters.auth/token->wire "random-token")))))
