(ns porteiro.adapters.password-reset
  (:require [schema.core :as s]
            [buddy.hashers :as hashers]
            [porteiro.wire.in.password-reset :as wire.in.password-reset]
            [porteiro.models.password-reset :as models.password])
  (:import (java.util UUID)))

(s/defn wire->password-reset-execution-internal :- models.password/PasswordResetExecution
  [{:keys [token newPassword]} :- wire.in.password-reset/PasswordResetExecution]
  #:password-reset-execution{:token           (UUID/fromString token)
                             :hashed-password (hashers/derive newPassword)})
