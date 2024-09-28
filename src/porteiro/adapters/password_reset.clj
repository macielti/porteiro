(ns porteiro.adapters.password-reset
  (:require [buddy.hashers :as hashers]
            [porteiro.models.password-reset :as models.password]
            [porteiro.wire.in.password-reset :as wire.in.password-reset]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn wire->password-reset-execution-internal :- models.password/PasswordResetExecution
  [{:keys [token newPassword]} :- wire.in.password-reset/PasswordResetExecution]
  {:password-reset-execution/token           (UUID/fromString token)
   :password-reset-execution/hashed-password (hashers/derive newPassword)})
