(ns porteiro.db.postgres.customer
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [schema.core :as s]
            [porteiro.models.customer :as models.customer]
            [porteiro.adapters.customer :as adapters.customer]
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

(s/defn lookup :- (s/maybe models.customer/Customer)        ;TODO: Add unit tests
  [customer-id :- s/Uuid
   database-connection]
  (jdbc/execute! database-connection
                 ["SELECT id, username, roles, hashed_password FROM customer WHERE id = ?"
                  customer-id]))