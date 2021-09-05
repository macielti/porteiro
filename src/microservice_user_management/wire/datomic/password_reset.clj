(ns microservice-user-management.wire.datomic.password-reset
  (:require [schema.core :as s])
  (:import (java.util Date)))

(def password-reset
  [{:db/ident       :password-reset/id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Password reset registry Id"}
   {:db/ident       :password-reset/user-id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc         "User id that own this password reset request"}
   {:db/ident       :password-reset/created-at
    :db/valueType   :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc         "Instant emission of the password request"}])

(s/defschema PasswordReset
  {:password-reset/id         s/Uuid
   :password-reset/user-id    s/Uuid
   :password-reset/created-at Date})
