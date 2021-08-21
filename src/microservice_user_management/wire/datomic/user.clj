(ns microservice-user-management.wire.datomic.user
  (:require [schema.core :as s]))

(def user
  [{:db/ident       :user/id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "User Id"}
   {:db/ident       :user/username
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "User username"}
   {:db/ident       :user/email
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "E-mail address"}
   {:db/ident       :user/password
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Hashed password"}])

(s/defschema User
  "Schema to represents a user entity"
  #:user {:id       s/Uuid
          :username s/Str
          :email    s/Str
          :password s/Str})
