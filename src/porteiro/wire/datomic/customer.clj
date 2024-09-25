(ns porteiro.wire.datomic.customer
  (:require [porteiro.models.customer :as models.customer]))

(def customer
  [{:db/ident       :customer/id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Customer Id"}
   {:db/ident       :customer/username
    :db/valueType   :db.type/string
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one
    :db/doc         "Customer username"}
   {:db/ident       :customer/roles
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/many
    :db/doc         "Customer roles"}
   {:db/ident       :customer/hashed-password
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Customer Hashed password"}])

(def Customer models.customer/Customer)
