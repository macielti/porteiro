(ns porteiro.adapters.user-test
  (:require [clj-uuid]
            [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [porteiro.adapters.customer :as adapters.user]
            [schema.test :as s])
  (:import (clojure.lang ExceptionInfo)
           (java.util UUID)))

(s/deftest wire->internal-user-test
  (testing "that we can verify the user schema input from outside"
    (is (match? {:customer/hashed-password string?
                 :customer/id              uuid?
                 :customer/username        "ednaldo-pereira"}
                (adapters.user/wire->internal-customer {:username "ednaldo-pereira"
                                                        :password "a-very-strong-password"}))))
  (testing "that a invalid input will throws a exception"
    (is (thrown? ExceptionInfo (adapters.user/wire->internal-customer {:username "ednaldo-pereira"
                                                                       :name     "Ednaldo Pereira"})))))

(s/deftest internal-user->wire-test
  (testing "externalize datomic query result for user entity"
    (is (match? {:id       clj-uuid/uuid-string?
                 :username "ednaldo-pereira"
                 :roles    []}
                (adapters.user/internal-customer->wire {:customer/id              (UUID/randomUUID)
                                                        :customer/username        "ednaldo-pereira"
                                                        :customer/hashed-password ""})))
    (is (match? {:id       clj-uuid/uuid-string?
                 :username "ednaldo-pereira"
                 :roles    ["ADMIN"]}
                (adapters.user/internal-customer->wire {:customer/id              (UUID/randomUUID)
                                                        :customer/roles           [:admin]
                                                        :customer/username        "ednaldo-pereira"
                                                        :customer/hashed-password ""})))))

(s/deftest wire->password-update-internal-test
  (testing "that we can internalise the password update input"
    (is (match? {:password-update/old-password "my-strong-and-secure-old-password"
                 :password-update/new-password "my-strong-and-secure-new-password"}
                (adapters.user/wire->password-update-internal {:oldPassword "my-strong-and-secure-old-password"
                                                               :newPassword "my-strong-and-secure-new-password"}))))
  (testing "input that does not match the input schema throws exception"
    (is (thrown? ExceptionInfo (adapters.user/wire->password-update-internal {:password    "wrong-one"
                                                                              :newPassword "i-do-not-know-what-i-am-doing"})))))
