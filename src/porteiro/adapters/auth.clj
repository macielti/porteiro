(ns porteiro.adapters.auth
  (:require [schema.core :as s]
            [porteiro.wire.in.auth :as wire.in.auth]
            [porteiro.wire.out.auth :as wire.out.auth]
            [porteiro.models.auth :as models.auth]))

(s/defn wire->internal-customer-auth :- models.auth/CustomerAuth
  [{:keys [username password]} :- wire.in.auth/CustomerAuth]
  {:customer-auth/username username
   :customer-auth/password password})

(s/defn token->wire :- wire.out.auth/Token
  [token :- s/Str]
  {:token token})
