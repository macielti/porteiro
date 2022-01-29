(ns porteiro.wire.in.password-reset
  (:require [schema.core :as s]))

(s/defschema PasswordResetExecution
  "Schema for password reset request"
  {:token       s/Str
   :newPassword s/Str})
