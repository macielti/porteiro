(ns porteiro.wire.datomic.user
  (:require [schema.core :as s]))

(def user
  [{:db/ident       :user/id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "User Id"}
   {:db/ident       :user/username
    :db/valueType   :db.type/string
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one
    :db/doc         "User username"}
   {:db/ident       :user/roles
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/many
    :db/doc         "User roles"}
   {:db/ident       :user/hashed-password
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Hashed password"}])

(def roles [:admin :test])

(def UserRoles (apply s/enum roles))

(s/defschema User
  {:user/id                     s/Uuid
   :user/username               s/Str
   (s/optional-key :user/email) s/Str
   (s/optional-key :user/roles) [UserRoles]
   :user/hashed-password        s/Str})
