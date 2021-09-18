(ns microservice-user-management.wire.datomic.session
  (:require [schema.core :as s])
  (:import (java.util Date)))

(def session
  [{:db/ident       :session/id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Session token entity Id"}
   {:db/ident       :session/user-id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc         "User id that own this session token"}
   {:db/ident       :session/secret
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc         "Secret to be able to validate JWTokens"}
   {:db/ident       :session/valid?
    :db/valueType   :db.type/boolean
    :db/cardinality :db.cardinality/one
    :db/doc         "Instant emission of the password request"}
   {:db/ident       :session/created-at
    :db/valueType   :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc         "Instant emission of the password request"}])

(s/defschema Session
  {:session/id         s/Uuid
   :session/user-id    s/Uuid
   :session/secret     s/Uuid
   :session/valid?     s/Bool
   :session/created-at Date})
