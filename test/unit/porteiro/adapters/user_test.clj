(ns porteiro.adapters.user-test
  (:require [clojure.test :refer :all]
            [schema.test :as s]
            [matcher-combinators.test :refer [match?]]
            [porteiro.adapters.user :as adapters.user])
  (:import (clojure.lang ExceptionInfo)
           (java.util UUID)))

(s/deftest wire->internal-user-test
  (testing "that we can verify the user schema input from outside"
    (is (match? {:user/email           "example@example.com"
                 :user/hashed-password string?
                 :user/id              uuid?
                 :user/username        "ednaldo-pereira"}
                (adapters.user/wire->internal-user {:username "ednaldo-pereira"
                                                    :email    "example@example.com"
                                                    :password "a-very-strong-password"}))))
  (testing "that a invalid input will throws a exception"
    (is (thrown? ExceptionInfo (adapters.user/wire->internal-user {:username "ednaldo-pereira"
                                                                   :name     "Ednaldo Pereira"})))))

(s/deftest internal-user->wire-test
  (testing "externalize datomic query result for user entity"
    (is (match? {:id       string?
                 :username "ednaldo-pereira"
                 :email    "example@example.com"}
                (adapters.user/internal-user->wire #:user{:id              (UUID/randomUUID)
                                                          :username        "ednaldo-pereira"
                                                          :email           "example@example.com"
                                                          :hashed-password ""})))))

(s/deftest wire->password-update-internal-test
  (testing "that we can internalise the password update input"
    (is (match? {:password-update/old-password "my-strong-and-secure-old-password"
                 :password-update/new-password "my-strong-and-secure-new-password"}
                (adapters.user/wire->password-update-internal {:oldPassword "my-strong-and-secure-old-password"
                                                               :newPassword "my-strong-and-secure-new-password"}))))
  (testing "input that does not match the input schema throws exception"
    (is (thrown? ExceptionInfo (adapters.user/wire->password-update-internal {:password    "wrong-one"
                                                                              :newPassword "i-do-not-know-what-i-am-doing"})))))
