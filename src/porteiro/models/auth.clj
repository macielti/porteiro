(ns porteiro.models.auth
  (:require [schema.core :as s]))

(s/defschema UserAuthentication
  {:user-authentication/username s/Str
   :user-authentication/password s/Str})

(s/defschema AuthenticationResult
  {:authentication-result/token s/Str})
