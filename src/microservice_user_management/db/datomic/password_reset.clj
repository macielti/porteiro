(ns microservice-user-management.db.datomic.password-reset
  (:require [microservice-user-management.wire.datomic.password-reset :as wire.datomic.password-reset]
            [schema.core :as s]
            [datomic.api :as d]))

(s/defn insert! :- wire.datomic.password-reset/PasswordReset
  [password-reset :- wire.datomic.password-reset/PasswordReset
   datomic]
  (d/transact datomic [password-reset])
  password-reset)
