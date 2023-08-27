(ns porteiro.db.postgres.contact
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [next.jdbc :as jdbc]
            [schema.core :as s]
            [porteiro.models.contact :as models.contact]
            [next.jdbc.date-time]
            [porteiro.adapters.contact :as adapters.contact]))
(defn remove-nils
  [record]
  (into {} (filter second record)))
(s/defn insert!
  [{:contact/keys [id user-id type status created-at chat-id email]} :- models.contact/Contact
   database-connection]
  (let [type' (camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING type)
        status' (camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING status)]
    (-> (jdbc/execute-one! database-connection
                           ["INSERT INTO contact (id, user_id, type, status, created_at, chat_id, email) VALUES (?, ?, ?, ?, ?, ?, ?)"
                            id user-id type' status' created-at chat-id email]
                           {:return-keys true})
        remove-nils
        adapters.contact/postgresql->internal)))

(s/defn by-customer-id :- (s/maybe [models.contact/Contact])
  [customer-id :- s/Uuid
   database-connection]
  (some->> (jdbc/execute! database-connection
                          ["SELECT id, user_id, type, status, created_at, chat_id, email FROM contact WHERE user_id = ?"
                           customer-id])
           (map remove-nils)
           (map adapters.contact/postgresql->internal)))

(s/defn by-email :- (s/maybe [models.contact/EmailContact])
  [email :- s/Str
   database-connection]
  (some->> (jdbc/execute! database-connection
                          ["SELECT id, user_id, type, status, created_at, chat_id, email FROM contact WHERE email = ?"
                           email])
           (map remove-nils)
           (map adapters.contact/postgresql->internal)))