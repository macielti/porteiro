(ns porteiro.models.auth
  (:require [schema.core :as s]))

(s/defschema CustomerAuth
  {:customer-auth/username s/Str
   :customer-auth/password s/Str})
