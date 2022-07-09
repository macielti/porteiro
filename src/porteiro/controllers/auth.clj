(ns porteiro.controllers.auth
  (:require [schema.core :as s]
            [buddy.hashers :as hashers]
            [common-clj.error.core :as common-error]
            [common-clj.auth.core :as common-auth]
            [porteiro.models.auth :as models.auth]
            [porteiro.adapters.user :as adapters.user]
            [porteiro.db.datomic.contact :as database.contact]
            [porteiro.db.datomic.user :as datomic.user]
            [porteiro.diplomat.producer :as diplomat.producer]))

(s/defn user-authentication! :- s/Str
  [{:user-auth/keys [username password]} :- models.auth/UserAuth
   {:keys [jwt-secret]}
   producer
   datomic]
  (let [{:user/keys [hashed-password id] :as user} (datomic.user/by-username username datomic)
        {:contact/keys [email] :as contact} (first (database.contact/by-user-id id datomic))]
    (if (and user (:valid (hashers/verify password hashed-password)))
      (do (diplomat.producer/send-success-auth-notification! email producer)
          (-> (adapters.user/internal-user->wire user email)
              (common-auth/->token jwt-secret)))
      (common-error/http-friendly-exception 403
                                            "invalid-credentials"
                                            "Wrong username or/and password"
                                            "user is trying to login using invalid credentials"))))
