(ns porteiro.models.password-reset
  (:require [schema.core :as s]))

(s/defschema PasswordResetExecution
  "Schema for password reset execution internal entity input"
  #:password-reset-execution{:token           s/Uuid
                               :hashed-password s/Str})
