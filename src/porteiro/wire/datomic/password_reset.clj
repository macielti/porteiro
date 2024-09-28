(ns porteiro.wire.datomic.password-reset
  (:require [schema.core :as s])
  (:import (java.util Date)))

(def password-reset
  [{:db/ident       :password-reset/id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Password reset registry Id"}
   {:db/ident       :password-reset/customer-id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc         "Customer id that own this password reset request"}
   {:db/ident       :password-reset/state
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc         "State of the token (:free, :used)"}
   {:db/ident       :password-reset/created-at
    :db/valueType   :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc         "Instant emission of the password request"}])

(s/defschema PasswordResetState (s/enum :free :used))

(s/defschema PasswordReset
  {:password-reset/id          s/Uuid
   :password-reset/customer-id s/Uuid
   :password-reset/state       PasswordResetState
   :password-reset/created-at  Date})
