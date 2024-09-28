(ns porteiro.models.password-reset
  (:require [schema.core :as s])
  (:import (java.util Date)))

(s/defschema PasswordResetExecution
  "Schema for password reset execution internal entity input"
  {:password-reset-execution/token           s/Uuid
   :password-reset-execution/hashed-password s/Str})

(def password-reset-statuses #{:free :used})
(s/defschema PasswordResetState (apply s/enum password-reset-statuses))

(s/defschema PasswordReset
  {:password-reset/id          s/Uuid
   :password-reset/customer-id s/Uuid
   :password-reset/state       PasswordResetState
   :password-reset/created-at  Date})
