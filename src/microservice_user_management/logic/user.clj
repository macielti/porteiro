(ns microservice-user-management.logic.user
  (:require [schema.core :as s]
            [microservice-user-management.wire.datomic.user :as wire.datomic.user]))

(s/defn old-valid-password? :- s/Bool
  [new-password :- s/
   {:user/keys [hashed-password]} :- wire.datomic.user/User])
