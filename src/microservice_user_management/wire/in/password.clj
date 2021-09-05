(ns microservice-user-management.wire.in.password
  (:require [schema.core :as s]))

(s/defschema PasswordResetConsolidation
  "Schema for password reset request"
  {:token       s/Str
   :newPassword s/Str})
