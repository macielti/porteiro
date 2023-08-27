(ns porteiro.db.postgres.customer-test
  (:require [clojure.test :refer :all])
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [fixtures.customer]
            [porteiro.db.postgres.customer :as database.customer]
            [porteiro.db.postgres.contact :as database.contact]
            [schema.test :as s]
            [fixtures.contact]))

(s/deftest insert-test
  (let [connection (-> (jdbc/get-connection {:jdbcUrl "jdbc:postgres://localhost:5432/postgres?user=postgres&password=postgres"})
                       (jdbc/with-options {:builder-fn rs/as-unqualified-maps}))
        schema-sql (slurp "resources/schema.sql")]

    (jdbc/execute! connection ["DROP TABLE IF EXISTS customer"])
    (jdbc/execute! connection [schema-sql])

    (testing "that we can insert a customer entity"
      (is (= fixtures.customer/customer
             (database.customer/insert! fixtures.customer/customer connection))))))

(s/deftest lookup-test
  (let [connection (-> (jdbc/get-connection {:jdbcUrl "jdbc:postgres://localhost:5432/postgres?user=postgres&password=postgres"})
                       (jdbc/with-options {:builder-fn rs/as-unqualified-maps}))
        schema-sql (slurp "resources/schema.sql")]

    (jdbc/execute! connection ["DROP TABLE IF EXISTS customer"])
    (jdbc/execute! connection [schema-sql])
    (database.customer/insert! fixtures.customer/customer connection)

    (testing "that we can search a existent customer by it's id"
      (is (= fixtures.customer/customer
             (database.customer/lookup fixtures.customer/customer-id connection))))

    (testing "that we can search a customer that does not exists"
      (is (nil? (database.customer/lookup (random-uuid) connection))))))

(s/deftest add-role-test
  (let [connection (-> (jdbc/get-connection {:jdbcUrl "jdbc:postgres://localhost:5432/postgres?user=postgres&password=postgres"})
                       (jdbc/with-options {:builder-fn rs/as-unqualified-maps}))
        schema-sql (slurp "resources/schema.sql")]

    (jdbc/execute! connection ["DROP TABLE IF EXISTS customer"])
    (jdbc/execute! connection [schema-sql])
    (database.customer/insert! (assoc fixtures.customer/customer :customer/roles [:admin]) connection)

    (testing "that we can add a role to a customer"
      (is (= fixtures.customer/customer
             (database.customer/add-role! fixtures.customer/customer-id :test connection))))))

(s/deftest by-username-test
  (let [connection (-> (jdbc/get-connection {:jdbcUrl "jdbc:postgres://localhost:5432/postgres?user=postgres&password=postgres"})
                       (jdbc/with-options {:builder-fn rs/as-unqualified-maps}))
        schema-sql (slurp "resources/schema.sql")]

    (jdbc/execute! connection ["DROP TABLE IF EXISTS customer"])
    (jdbc/execute! connection [schema-sql])
    (database.customer/insert! fixtures.customer/customer connection)

    (testing "that we can search a existent customer by it's username"
      (is (= fixtures.customer/customer
             (database.customer/by-username fixtures.customer/customer-username connection))))

    (testing "that we can search a customer that does not exists"
      (is (nil? (database.customer/by-username "not-exists" connection))))))

(s/deftest insert-user-with-contact-test
  (let [connection (-> (jdbc/get-connection {:jdbcUrl "jdbc:postgres://localhost:5432/postgres?user=postgres&password=postgres"})
                       (jdbc/with-options {:builder-fn rs/as-unqualified-maps}))
        schema-sql (slurp "resources/schema.sql")]

    (jdbc/execute! connection ["DROP TABLE IF EXISTS customer"])
    (jdbc/execute! connection ["DROP TABLE IF EXISTS contact"])
    (jdbc/execute! connection [schema-sql])

    (testing "that we can insert a customer along with contact in a single one transaction"
      (database.customer/insert-user-with-contact! fixtures.customer/customer fixtures.contact/contact connection)
      (is (= fixtures.customer/customer
             (database.customer/lookup fixtures.customer/customer-id connection)))

      (is (= [fixtures.contact/contact]
             (database.contact/by-customer-id fixtures.customer/customer-id connection))))))
