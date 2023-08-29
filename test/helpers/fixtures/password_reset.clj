(ns fixtures.password-reset
  (:require [clojure.test :refer :all]
            [fixtures.user]
            [porteiro.models.password-reset :as models.password-reset]
            [porteiro.wire.datomic.password-reset :as wire.datomic.password-reset]
            [schema-generators.complete :as c]
            [porteiro.wire.postgresql.password-reset :as wire.postgresql.password-reset])
  (:import (java.util Date)))

(def created-at (Date.))
(def password-reset-id (random-uuid))
(def datalevin-password-reset (c/complete {:password-reset/id         password-reset-id
                                           :password-reset/user-id    fixtures.user/user-id
                                           :password-reset/state      :free
                                           :password-reset/created-at created-at} wire.datomic.password-reset/PasswordReset))

(def password-reset (c/complete {:password-reset/id         password-reset-id
                                 :password-reset/user-id    fixtures.user/user-id
                                 :password-reset/state      :free
                                 :password-reset/created-at created-at} models.password-reset/PasswordReset))

(def password-reset-postgresql (c/complete {:id         password-reset-id
                                            :user_id    fixtures.user/user-id
                                            :state      "FREE"
                                            :created_at created-at} wire.postgresql.password-reset/PasswordReset))
