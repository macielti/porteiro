(ns porteiro.adapters.auth-test
  (:require [clojure.test :refer :all]
            [schema.test :as s]
            [matcher-combinators.test :refer [match?]]
            [porteiro.adapters.auth :as adapters.auth])
  (:import (clojure.lang ExceptionInfo)))

(s/deftest wire->internal-user-auth-test
  (testing "that we can verify the user schema input from outside"
    (is (= {:user-auth/password "a-very-strong-password"
            :user-auth/username "ednaldo-pereira"}
           (adapters.auth/wire->internal-user-auth {:username "ednaldo-pereira"
                                                    :password "a-very-strong-password"}))))
  (testing "that a invalid input will throws a exception"
    (is (thrown? ExceptionInfo (adapters.auth/wire->internal-user-auth {:username "ednaldo-pereira"
                                                                        :name     "Ednaldo Pereira"})))))

(s/deftest token->wire-test
  (testing "that we can externalize token value to wire out"
    (is (= {:token "random-token"}
           (adapters.auth/token->wire "random-token")))))
