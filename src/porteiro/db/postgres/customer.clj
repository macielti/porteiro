(ns porteiro.db.postgres.customer
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [schema.core :as s]
            [porteiro.models.customer :as models.user]
            [next.jdbc :as jdbc]
            [next.jdbc.types :as jdbc.types]))

(s/defn insert! :- models.user/Customer
  [{:customer/keys [id username roles hashed-password] :as customer} :- models.user/Customer
   database-connection]
  (s/validate models.user/Customer customer)
  (let [roles' (into-array (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING roles))]
    (jdbc/execute! database-connection ["INSERT INTO customer (id, username, roles, hashed_password) VALUES (?, ?, ?, ?)"
                                        id username roles' hashed-password]))
  customer)