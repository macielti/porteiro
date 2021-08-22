(ns microservice-user-management.wire.out.user
  (:require [schema.core :as s]))

(s/defschema User
  "Schema for user creation request"
  {:id       s/Str
   :username s/Str
   :email    s/Str})
