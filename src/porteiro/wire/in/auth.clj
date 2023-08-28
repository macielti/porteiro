(ns porteiro.wire.in.auth
  (:require [schema.core :as s]))

(s/defschema CustomerAuth
  "Schema for customer authentication request"
  {:username s/Str
   :password s/Str})
