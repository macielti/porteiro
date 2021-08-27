(ns microservice-user-management.adapters.auth-test
  (:require [clojure.test :refer :all]
            [microservice-user-management.adapters.auth :as adapters.auth]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]
            [buddy.sign.jwt :as jwt]
            [clj-time.coerce :as c]
            [clj-time.core :as t])
  (:import (clojure.lang ExceptionInfo)
           (java.util UUID)))

(use-fixtures :once s/validate-schemas)

(def jwt-secret "ey5CWUnp9YvYmsZZ66J1IM90LOzuP721")
(def user-entity {:id       (UUID/randomUUID)
                  :username "ednaldo-pereira"
                  :email    "example@example.com"})
(def jwt-token (jwt/sign user-entity jwt-secret
                         {:exp (-> (t/plus (t/now) (t/days 1))
                                   c/to-timestamp)}))

(s/deftest wire->internal-test
  (testing "that we can verify the user schema input from outside"
    (is (= {:username "ednaldo-pereira"
            :password "a-very-strong-password"}
           (adapters.auth/wire->internal {:username "ednaldo-pereira"
                                          :password "a-very-strong-password"}))))
  (testing "that a invalid input will throws a exception"
    (is (thrown? ExceptionInfo (adapters.auth/wire->internal {:username "ednaldo-pereira"
                                                              :name     "Ednaldo Pereira"})))))
(s/deftest jwt-wire->internal-test
  (testing "that we can internalize jwt tokens"
    (is (match? {:id       uuid?
                 :username "ednaldo-pereira"
                 :email    "example@example.com"
                 :exp      int?}
                (adapters.auth/jwt-wire->internal jwt-token jwt-secret)))))
