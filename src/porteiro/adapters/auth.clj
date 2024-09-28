(ns porteiro.adapters.auth
  (:require [porteiro.models.auth :as models.auth]
            [porteiro.wire.in.auth :as wire.in.auth]
            [porteiro.wire.out.auth :as wire.out.auth]
            [schema.core :as s]))

(s/defn wire->internal-customer-auth :- models.auth/CustomerAuth
  [{:keys [username password]} :- wire.in.auth/CustomerAuth]
  {:customer-auth/username username
   :customer-auth/password password})

(s/defn token->wire :- wire.out.auth/Token
  [token :- s/Str]
  {:token token})
