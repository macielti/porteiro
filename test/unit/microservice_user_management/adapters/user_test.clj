(ns microservice-user-management.adapters.user-test
  (:require [clojure.test :refer :all]
            [microservice-user-management.adapters.user :as adapters.user]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s])
  (:import (clojure.lang ExceptionInfo)))

(use-fixtures :once s/validate-schemas)

(s/deftest wire->internal-test
  (testing "that we can verify the user schema input from outside"
    (is (= {:username "ednaldo-pereira"
            :email    "example@example.com"
            :password "a-very-strong-password"}
           (adapters.user/wire->internal {:username "ednaldo-pereira"
                                          :email    "example@example.com"
                                          :password "a-very-strong-password"}))))
  (testing "that a invalid input will throws a exception"
    (is (thrown? ExceptionInfo (adapters.user/wire->internal {:username "ednaldo-pereira"
                                                              :name     "Ednaldo Pereira"})))))
(s/deftest internal->datomic-test
  (testing "that we can convert from internal model to datomic schema"
    (is (match? #:user {:id       uuid?
                        :username "ednaldo-pereira"
                        :email    "example@example.com"
                        :password string?}
                (adapters.user/internal->datomic {:username "ednaldo-pereira"
                                                  :email    "example@example.com"
                                                  :password "a-very-strong-password"})))))
