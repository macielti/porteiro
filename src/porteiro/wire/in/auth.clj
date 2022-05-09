(ns porteiro.wire.in.auth
  (:require [schema.core :as s]))

(def base-user-auth
  {:password s/Str})

(s/defschema UserAuthWithEmail
  (merge base-user-auth
         {:email s/Str}))

(s/defschema UserAuthWithUsername
  (merge base-user-auth
         {:username s/Str}))

(s/defschema UserAuth
  (s/conditional #(-> (:email %) boolean)
                 UserAuthWithEmail
                 #(-> (:username %) boolean)
                 UserAuthWithUsername))
