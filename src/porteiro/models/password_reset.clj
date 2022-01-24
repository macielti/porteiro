(ns microservice-user-management.models.password-reset
  (:require [schema.core :as s]))

(s/defschema PasswordResetConsolidation
  "Schema for password reset consolidation internal entity input"
  {:token        s/Uuid
   :hashed-password s/Str})
