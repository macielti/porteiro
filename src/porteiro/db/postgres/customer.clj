(ns porteiro.db.postgres.customer
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [porteiro.models.customer :as models.user]
            [porteiro.wire.datomic.user :as wire.datomic.user]
            [schema.core :as s]
            [porteiro.models.customer :as models.customer]
            [porteiro.adapters.customer :as adapters.customer]
            [porteiro.models.contact :as models.contact]
            [next.jdbc.date-time]
            [next.jdbc :as jdbc]))

(s/defn insert! :- models.customer/Customer
  [{:customer/keys [id username roles hashed-password] :as customer} :- models.customer/Customer
   database-connection]
  (s/validate models.customer/Customer customer)
  (let [roles' (into-array (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING roles))]
    (-> (jdbc/execute-one! database-connection
                           ["INSERT INTO customer (id, username, roles, hashed_password) VALUES (?, ?, ?, ?)"
                            id username roles' hashed-password]
                           {:return-keys true})
        adapters.customer/postgresql->internal)))

(s/defn lookup :- (s/maybe models.customer/Customer)
  [customer-id :- s/Uuid
   database-connection]
  (some-> (jdbc/execute-one! database-connection
                             ["SELECT id, username, roles, hashed_password FROM customer WHERE id = ?"
                              customer-id])
          adapters.customer/postgresql->internal))

(s/defn add-role! :- models.customer/Customer
  [user-id :- s/Uuid
   role :- wire.datomic.user/UserRoles
   database-connection]
  (-> (jdbc/execute-one! database-connection
                         ["UPDATE customer set roles = array_append(roles, ?) where id=?"
                          (camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING role) user-id]
                         {:return-keys true})
      adapters.customer/postgresql->internal))

(s/defn by-username :- (s/maybe models.user/Customer)
  [username :- s/Str
   database-connection]
  (some-> (jdbc/execute-one! database-connection
                             ["SELECT id, username, roles, hashed_password FROM customer WHERE username = ?"
                              username])
          adapters.customer/postgresql->internal))

(s/defn insert-user-with-contact!
  [customer :- models.user/Customer
   contact :- models.contact/Contact
   database-connection]
  (jdbc/with-transaction [tx database-connection]
    (let [customer-roles (some->> (:customer/roles customer)
                                  (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING)
                                  into-array)]
      (jdbc/execute-one! tx
                         ["INSERT INTO customer (id, username, roles, hashed_password) VALUES (?, ?, ?, ?)"
                          (:customer/id customer)
                          (:customer/username customer)
                          customer-roles
                          (:customer/hashed-password customer)]))
    (let [contact-type (camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING (:contact/type contact))
          contact-status (camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING (:contact/status contact))]
      (jdbc/execute-one! tx
                         ["INSERT INTO contact (id, user_id, type, status, created_at, chat_id, email) VALUES (?, ?, ?, ?, ?, ?, ?)"
                          (:contact/id contact)
                          (:contact/user-id contact)
                          contact-type
                          contact-status
                          (:contact/created-at contact)
                          (:contact/chat-id contact)
                          (:contact/email contact)]))))