(ns microservice-user-management.controllers.password-reset
  (:require [schema.core :as s]
            [microservice-user-management.adapters.password :as adapters.password]
            [microservice-user-management.wire.in.password :as wire.in.password]
            [microservice-user-management.db.datomic.password-reset :as datomic.password-reset]
            [microservice-user-management.db.datomic.user :as datomic.user]
            [buddy.hashers :as hashers]))


(s/defn consolidate-password-reset!
  [password-reset :- wire.in.password/PasswordResetConsolidation
   datomic]
  (let [{:keys [token hashed-password]} (adapters.password/wire->password-reset-consolidation-internal password-reset)
        {:password-reset/keys [user-id]} (datomic.password-reset/valid-password-reset-by-token {:token token} datomic)
        user (datomic.user/by-id user-id datomic)]
    (-> (assoc user :user/hashed-password hashed-password)
        (datomic.user/insert! datomic))))
