(ns porteiro.adapters.password-reset
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [porteiro.models.password-reset :as models.password-reset]
            [schema.core :as s]
            [buddy.hashers :as hashers]
            [porteiro.wire.in.password-reset :as wire.in.password-reset]
            [porteiro.models.password-reset :as models.password]
            [porteiro.wire.postgresql.password-reset :as wire.postgresql.password-reset])
  (:import (java.util Date UUID)))

(s/defn wire->password-reset-execution-internal :- models.password/PasswordResetExecution
  [{:keys [token newPassword]} :- wire.in.password-reset/PasswordResetExecution]
  {:password-reset-execution/token           (UUID/fromString token)
   :password-reset-execution/hashed-password (hashers/derive newPassword)})

(s/defn postgresql->internal :- models.password-reset/PasswordReset
  [{:keys [id user_id state created_at]} :- wire.postgresql.password-reset/PasswordReset]
  {:password-reset/id         id
   :password-reset/user-id    user_id
   :password-reset/state      (camel-snake-kebab/->kebab-case-keyword state)
   :password-reset/created-at created_at})
