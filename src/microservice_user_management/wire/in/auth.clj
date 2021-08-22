(ns microservice-user-management.wire.in.auth
  (:require [schema.core :as s]))

(s/defschema Auth
  "Schema for user authentication request"
  {:username s/Str
   :password s/Str})
