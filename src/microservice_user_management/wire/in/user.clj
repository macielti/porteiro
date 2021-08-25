(ns microservice-user-management.wire.in.user
  (:require [schema.core :as s]))

(s/defschema User
  "Schema for user creation request"
  {:username s/Str
   :password s/Str
   :email    s/Str})

(s/defschema PasswordUpdate
  "Schema for password update request"
  {:old-password s/Str
   :new-password s/Str})
