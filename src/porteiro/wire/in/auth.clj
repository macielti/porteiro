(ns porteiro.wire.in.auth
  (:require [schema.core :as s]))

(s/defschema UserAuth
  "Schema for user authentication request"
  {:username s/Str
   :password s/Str})
