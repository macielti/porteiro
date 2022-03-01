(ns porteiro.adapters.auth
  (:require [schema.core :as s]
            [porteiro.wire.in.auth :as wire.in.auth]
            [porteiro.models.auth :as models.auth]))

(s/defn wire->internal-user-auth :- models.auth/UserAuth
  [{:keys [username password]} :- wire.in.auth/UserAuth]
  {:user-auth/username username
   :user-auth/password password})
