(ns microservice-user-management.controllers.auth
  (:use [clojure pprint])
  (:require [schema.core :as s]
            [microservice-user-management.adapters.auth :as adapters.auth]
            [microservice-user-management.wire.in.auth :as wire.in.auth]
            [microservice-user-management.db.datomic.user :as datomic.user]
            [microservice-user-management.adapters.user :as adapters.user]
            [buddy.sign.jwt :as jwt]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [buddy.hashers :as hashers]))

(s/defn auth
  [{:keys [username password] :as auth} :- wire.in.auth/Auth
   {:keys [jw-token-secret]}
   database]
  (adapters.auth/wire->internal auth)
  (let [{:user/keys [hashed-password] :as user} (datomic.user/by-username username database)]
    (if (and user
             (:valid (hashers/verify password hashed-password)))
      {:token (jwt/sign (adapters.user/datomic->wire user)
                        jw-token-secret
                        {:exp (-> (t/plus (t/now) (t/days 1))
                                  c/to-timestamp)})}
      (throw (ex-info "Wrong username or/and password"
                      {:status 403
                       :reason "Wrong username or/and password"})))))
