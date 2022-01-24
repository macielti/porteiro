(ns porteiro.controllers.auth
  (:require [schema.core :as s]
            [porteiro.adapters.auth :as adapters.auth]
            [porteiro.wire.in.auth :as wire.in.auth]
            [porteiro.db.datomic.user :as datomic.user]
            [porteiro.adapters.user :as adapters.user]
            [porteiro.adapters.session :as adapters.session]
            [porteiro.db.datomic.session :as datomic.session]
            [porteiro.diplomatic.producer :as diplomatic.producer]
            [buddy.sign.jwt :as jwt]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [buddy.hashers :as hashers])
  (:import (java.util UUID)))

(s/defn auth
  [{:keys [username password] :as auth} :- wire.in.auth/Auth
   producer
   database]
  (adapters.auth/wire->internal auth)
  (let [{:user/keys [hashed-password id email] :as user} (datomic.user/by-username username database)]
    (if (and user
             (:valid (hashers/verify password hashed-password)))
      (let [jw-token-secret (UUID/randomUUID)]
        (diplomatic.producer/send-success-auth-notification! email producer)
        (-> (adapters.session/->datomic id jw-token-secret)
            (datomic.session/insert! database))
        {:token (jwt/sign (adapters.user/datomic->wire user) ;TODO: abstract it to a separated function
                          (str jw-token-secret)
                          {:exp (-> (t/plus (t/now) (t/days 1)) ;TODO: get the expiration time from config component
                                    c/to-timestamp)})})
      (throw (ex-info "Wrong username or/and password"
                      {:status 403
                       :reason "Wrong username or/and password"})))))
