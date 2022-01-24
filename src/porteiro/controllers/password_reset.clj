(ns porteiro.controllers.password-reset
  (:require [schema.core :as s]
            [porteiro.adapters.password-reset :as adapters.password-reset]
            [porteiro.wire.in.password-reset :as wire.in.password-reset]
            [porteiro.db.datomic.password-reset :as datomic.password-reset]
            [porteiro.db.datomic.user :as datomic.user]))


(s/defn consolidate-password-reset!
  [password-reset :- wire.in.password-reset/PasswordResetConsolidation
   datomic]
  (let [{:keys [token hashed-password]} (adapters.password-reset/wire->password-reset-consolidation-internal password-reset)
        {:password-reset/keys [user-id] :as password-reset-datomic} (datomic.password-reset/valid-and-free-password-reset-by-token {:token token} datomic)
        user (datomic.user/by-id user-id datomic)]
    (datomic.password-reset/insert! (assoc password-reset-datomic :password-reset/state :used) datomic)
    (-> (assoc user :user/hashed-password hashed-password)
        (datomic.user/insert! datomic))))
