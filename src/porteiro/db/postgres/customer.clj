(ns porteiro.db.postgres.customer
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [porteiro.models.customer :as models.user]
            [porteiro.wire.datomic.user :as wire.datomic.user]
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