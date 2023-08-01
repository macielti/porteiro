(ns porteiro.controllers.password-reset
  (:require [datalevin.core :as d]
            [schema.core :as s]
            [porteiro.models.password-reset :as models.password]
            [porteiro.db.datalevin.password-reset :as database.password-reset]
            [porteiro.db.datalevin.user :as database.user]))


(s/defn execute-password-reset!
  [{:password-reset-execution/keys [token hashed-password]} :- models.password/PasswordResetExecution
   datalevin-connection]
  (let [{:password-reset/keys [id user-id]} (database.password-reset/valid-password-reset-by-token token
                                                                                                   (d/db datalevin-connection))
        user (database.user/lookup user-id (d/db datalevin-connection))]
    (database.password-reset/set-as-used! id datalevin-connection)
    (-> (assoc user :user/hashed-password hashed-password)
        (database.user/insert! datalevin-connection))))
