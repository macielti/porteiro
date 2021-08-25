(ns microservice-user-management.adapters.user-test
  (:require [clojure.test :refer :all]
            [microservice-user-management.adapters.user :as adapters.user]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s])
  (:import (clojure.lang ExceptionInfo)
           (java.util UUID)))

(use-fixtures :once s/validate-schemas)

(s/deftest wire->internal-test
  (testing "that we can verify the user schema input from outside"
    (is (= {:username "ednaldo-pereira"
            :email    "example@example.com"
            :password "a-very-strong-password"}
           (adapters.user/wire->create-user-internal {:username "ednaldo-pereira"
                                          :email                "example@example.com"
                                          :password             "a-very-strong-password"}))))
  (testing "that a invalid input will throws a exception"
    (is (thrown? ExceptionInfo (adapters.user/wire->create-user-internal {:username "ednaldo-pereira"
                                                              :name                 "Ednaldo Pereira"})))))
(s/deftest internal->datomic-test
  (testing "that we can convert from internal model to datomic schema"
    (is (match? #:user {:id              uuid?
                        :username        "ednaldo-pereira"
                        :email           "example@example.com"
                        :hashed-password string?}
                (adapters.user/internal->create-user-datomic {:username "ednaldo-pereira"
                                                  :email                "example@example.com"
                                                  :password             "a-very-strong-password"})))))

(s/deftest datomic->wire-test
  (testing "externalize datomic query result for user entity"
    (is (match? {:id       string?
                 :username "ednaldo-pereira"
                 :email    "example@example.com"}
                (adapters.user/datomic->wire #:user{:id              (UUID/randomUUID)
                                                    :username        "ednaldo-pereira"
                                                    :email           "example@example.com"
                                                    :hashed-password ""})))))
