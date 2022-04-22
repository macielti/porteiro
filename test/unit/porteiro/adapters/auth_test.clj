(ns porteiro.adapters.auth-test
  (:require [clojure.test :refer :all]
            [schema.test :as s]
            [fixtures.user]
            [fixtures.authentication]
            [matcher-combinators.test :refer [match?]]
            [porteiro.adapters.auth :as adapters.auth])
  (:import (clojure.lang ExceptionInfo)))

(s/deftest wire->internal-user-auth-test
  (testing "that we can verify the user schema input from outside"
    (is (= {:user-authentication/password "random-password"
            :user-authentication/username "ednaldo-pereira"}
           (adapters.auth/wire->internal-user-auth {:username fixtures.user/username
                                                    :password fixtures.user/password}))))
  (testing "that a invalid input will throws a exception"
    (is (thrown? ExceptionInfo (adapters.auth/wire->internal-user-auth {:username fixtures.user/username
                                                                        :name     "Ednaldo Pereira"})))))

(s/deftest user-wire->internal-user-auth-test
  (testing "that we can convert a user wire creation input to internal user authentication entity"
    (is (= {:user-authentication/username "ednaldo-pereira"
            :user-authentication/password "random-password"}
           (adapters.auth/user-wire->internal-user-auth {:username fixtures.user/username
                                                         :email    fixtures.user/email
                                                         :password fixtures.user/password})))))

(deftest authentication-result->wire-test
  (testing "that we can convert a internal user authentication result to a wire entity"
    (is (= {:token "random-token"}
           (adapters.auth/authentication-result->wire {:authentication-result/token fixtures.authentication/minimal-token})))))
