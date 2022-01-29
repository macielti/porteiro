(ns porteiro.controllers.password-reset
  (:require [schema.core :as s]
            [porteiro.models.password-reset :as models.password]
            [porteiro.db.datomic.password-reset :as datomic.password-reset]
            [porteiro.db.datomic.user :as datomic.user]))


(s/defn execute-password-reset!
  [{:password-reset-execution/keys [token hashed-password]} :- models.password/PasswordResetExecution
   datomic]
  (let [{:password-reset/keys [user-id] :as password-reset-datomic} (datomic.password-reset/valid-password-reset-by-token token
                                                                                                                          datomic)
        user (datomic.user/by-id user-id datomic)]
    (datomic.password-reset/insert! (assoc password-reset-datomic :password-reset/state :used) datomic)
    (-> (assoc user :user/hashed-password hashed-password)
        (datomic.user/insert! datomic))))
