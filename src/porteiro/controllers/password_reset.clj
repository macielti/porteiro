(ns porteiro.controllers.password-reset
  (:require [datomic.api :as d]
            [porteiro.db.datomic.customer :as database.customer]
            [porteiro.db.datomic.password-reset :as database.password-reset]
            [porteiro.models.password-reset :as models.password]
            [schema.core :as s]))

(s/defn execute-password-reset!
  [{:password-reset-execution/keys [token hashed-password]} :- models.password/PasswordResetExecution
   datomic]
  (let [{:password-reset/keys [id customer-id]} (database.password-reset/valid-password-reset-by-token token
                                                                                                       (d/db datomic))
        customer (database.customer/lookup customer-id (d/db datomic))]
    (database.password-reset/set-as-used! id datomic)
    (-> (assoc customer :customer/hashed-password hashed-password)
        (database.customer/insert! datomic))))
