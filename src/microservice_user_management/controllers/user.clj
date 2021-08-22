(ns microservice-user-management.controllers.user
  (:use [clojure pprint])
  (:require [schema.core :as s]
            [microservice-user-management.wire.datomic.user :as wire.datomic.user]
            [microservice-user-management.wire.in.user :as wire.in.user]
            [microservice-user-management.adapters.user :as adapters.user]
            [microservice-user-management.db.datomic.user :as datomic.user]))

(s/defn create-user! :- wire.datomic.user/User
  [user :- wire.in.user/User
   datomic]
  (-> user
      adapters.user/wire->internal
      adapters.user/internal->datomic
      (datomic.user/insert! datomic)
      adapters.user/datomic->wire))
