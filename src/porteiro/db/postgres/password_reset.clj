(ns porteiro.db.postgres.password-reset
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [porteiro.adapters.password-reset :as adapters.password-reset]
            [schema.core :as s]
            [porteiro.models.password-reset :as models.password-reset]
            [next.jdbc.date-time]
            [next.jdbc :as jdbc]))

(s/defn insert! :- models.password-reset/PasswordReset
  [{:password-reset/keys [id user-id state created-at]} :- models.password-reset/PasswordReset
   database-connection]
  (let [state' (camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING state)]
    (-> (jdbc/execute-one! database-connection
                           ["INSERT INTO password_reset (id, user_id, state, created_at) VALUES (?, ?, ?, ?)"
                            id user-id state' created-at]
                           {:return-keys true})
        adapters.password-reset/postgresql->internal)))

(s/defn lookup :- (s/maybe models.password-reset/PasswordReset)
  [password-reset-id :- s/Uuid
   database-connection]
  (some-> (jdbc/execute-one! database-connection
                             ["SELECT id, user_id, state, created_at FROM password_reset WHERE id = ?"
                              password-reset-id])
          adapters.password-reset/postgresql->internal))