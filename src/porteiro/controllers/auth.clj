(ns porteiro.controllers.auth
  (:require [datalevin.core :as d]
            [schema.core :as s]
            [buddy.hashers :as hashers]
            [common-clj.error.core :as common-error]
            [common-clj.auth.core :as common-auth]
            [porteiro.models.auth :as models.auth]
            [porteiro.adapters.customer :as adapters.user]
            [porteiro.db.datalevin.contact :as database.contact]
            [porteiro.db.datalevin.user :as database.user]
            [porteiro.diplomat.producer :as diplomat.producer]))

(s/defn user-authentication! :- s/Str
  [{:user-auth/keys [username password]} :- models.auth/UserAuth
   {:keys [jwt-secret]}
   producer
   datalevin-connection]
  (let [database-snapshot (d/db datalevin-connection)
        {:user/keys [hashed-password id] :as user} (database.user/by-username username database-snapshot)
        {:contact/keys [email]} (first (database.contact/by-user-id id database-snapshot))]
    (if (and user (:valid (hashers/verify password hashed-password)))
      (do (diplomat.producer/send-success-auth-notification! email producer)
          (-> {:user (adapters.user/internal-customer->wire user)}
              (common-auth/->token jwt-secret)))
      (common-error/http-friendly-exception 403
                                            "invalid-credentials"
                                            "Wrong username or/and password"
                                            "user is trying to login using invalid credentials"))))
