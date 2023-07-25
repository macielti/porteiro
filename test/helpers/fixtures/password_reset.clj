(ns fixtures.password-reset
  (:require [clojure.test :refer :all]
            [fixtures.user]
            [porteiro.wire.datomic.password-reset :as wire.datomic.password-reset]
            [schema-generators.complete :as c])
  (:import (java.util Date)))

(def created-at (Date.))
(def password-reset-id (random-uuid))
(def datalevin-password-reset (c/complete {:password-reset/id         password-reset-id
                                           :password-reset/user-id    fixtures.user/user-id
                                           :password-reset/state      :free
                                           :password-reset/created-at created-at} wire.datomic.password-reset/PasswordReset))
