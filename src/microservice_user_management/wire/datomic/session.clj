(ns microservice-user-management.wire.datomic.session
  (:require [schema.core :as s])
  (:import (java.util Date)))

(def session
  [{:db/ident       :session-token/id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Session token entity Id"}
   {:db/ident       :session-token/user-id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc         "User id that own this session token"}
   {:db/ident       :session-token/secret
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc         "Secret to be able to validate JWTokens"}
   {:db/ident       :session-token/created-at
    :db/valueType   :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc         "Instant emission of the password request"}])

(s/defschema Session
  {:session-token/id         s/Uuid
   :session-token/user-id    s/Uuid
   :session-token/secret     s/Uuid
   :session-token/created-at Date})
