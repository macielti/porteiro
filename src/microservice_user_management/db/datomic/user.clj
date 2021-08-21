(ns microservice-user-management.db.datomic.user
  (:require [schema.core :as s]
            [microservice-user-management.wire.datomic.user :as wire.datomic.user]
            [datomic.api :as d]))

(s/defn insert! :- wire.datomic.user/User
  [user :- wire.datomic.user/User
   datomic]
  (d/transact datomic [user])
  user)
