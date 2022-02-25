(ns porteiro.controllers.auth
  (:require [schema.core :as s]
            [buddy.sign.jwt :as jwt]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [buddy.hashers :as hashers]
            [porteiro.models.auth :as models.auth]
            [porteiro.models.user :as models.user]
            [porteiro.models.contact :as models.contact]
            [porteiro.adapters.user :as adapters.user]
            [porteiro.db.datomic.contact :as database.contact]
            [porteiro.db.datomic.user :as datomic.user]
            [porteiro.diplomatic.producer :as diplomatic.producer]))

(s/defn ^:private ->token :- s/Str
  [user :- models.user/User
   {:contact/keys [email]} :- models.contact/Contact
   jwt-secret :- s/Str]
  (jwt/sign (adapters.user/internal-user->wire user email)
            jwt-secret
            {:exp (-> (t/plus (t/now) (t/days 1))
                      c/to-timestamp)}))

(s/defn user-authentication!
  [{:user-auth/keys [username password]} :- models.auth/UserAuth
   {:keys [jwt-secret]}
   producer
   datomic]
  (let [{:user/keys [hashed-password id] :as user} (datomic.user/by-username username datomic)
        {:contact/keys [email] :as contact} (first (database.contact/by-user-id id datomic))]
    (if (and user (:valid (hashers/verify password hashed-password)))
      (do (diplomatic.producer/send-success-auth-notification! email producer)
          {:token (->token user contact jwt-secret)})
      (throw (ex-info "Wrong username or/and password"
                      {:status 403
                       :cause  "Wrong username or/and password"})))))
