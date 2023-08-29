(ns porteiro.wire.postgresql.password-reset
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [porteiro.models.password-reset :as models.password-reset]
            [schema.core :as s])
  (:import (java.util Date)))

(def password-reset-statuses (->> models.password-reset/password-reset-statuses
                                 (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING)
                                 set))
(s/defschema PasswordResetState (apply s/enum password-reset-statuses))

(s/defschema PasswordReset
  {:id         s/Uuid
   :user_id    s/Uuid
   :state      PasswordResetState
   :created_at Date})