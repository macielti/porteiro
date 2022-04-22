(ns porteiro.adapters.auth
  (:require [schema.core :as s]
            [porteiro.wire.in.auth :as wire.in.auth]
            [porteiro.wire.in.user :as wire.in.user]
            [porteiro.wire.out.auth :as wire.out.auth]
            [porteiro.models.auth :as models.auth]))

(s/defn wire->internal-user-auth :- models.auth/UserAuthentication
  [{:keys [username password]} :- wire.in.auth/UserAuth]
  {:user-authentication/username username
   :user-authentication/password password})

(s/defn user-wire->internal-user-auth :- models.auth/UserAuthentication
  [{:keys [username password]} :- wire.in.user/User]
  {:user-authentication/username username
   :user-authentication/password password})

(s/defn authentication-result->wire :- wire.out.auth/AuthenticationResult
  [{:authentication-result/keys [token]} :- models.auth/AuthenticationResult]
  {:token token})
