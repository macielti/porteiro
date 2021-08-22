(ns microservice-user-management.controllers.auth
  (:require [schema.core :as s]
            [microservice-user-management.adapters.auth :as adapters.auth]
            [microservice-user-management.wire.in.auth :as wire.in.auth]))

(s/defn auth
  [auth :- wire.in.auth/Auth
   {{:keys [jw-token-secret]} :config}
   datomic]
  (-> auth
      adapters.auth/wire->internal))

(s/defn auth
  [{:keys [username password]} :- wire.in.auth/Auth
   {{:keys [jw-token-secret]} :config}
   database]
  (let [{:user/keys [password] :as user} (datomic.user/by-username username database)]
    (if (and user
             (:valid (hashers/verify password hashed-password)))
      {:token (jwt/sign (adapters.user/->wire user)
                        jw-token-secret
                        {:exp (-> (t/plus (t/now) (t/days 1))
                                  c/to-timestamp)})}
      (throw (ex-info "Wrong username or/and password"
                      {:status 403
                       :reason "Wrong username or/and password"})))))
