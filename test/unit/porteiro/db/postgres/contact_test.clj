(ns porteiro.db.postgres.contact-test
  (:require [clojure.test :refer :all]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [porteiro.db.postgres.contact :as database.contact]
            [fixtures.customer]
            [fixtures.contact]
            [schema.test :as s]))

(s/deftest insert-test
  (let [connection (-> (jdbc/get-connection {:jdbcUrl "jdbc:postgres://localhost:5432/postgres?user=postgres&password=postgres"})
                       (jdbc/with-options {:builder-fn rs/as-unqualified-maps}))
        schema-sql (slurp "resources/schema.sql")]

    (jdbc/execute! connection ["DROP TABLE IF EXISTS contact"])
    (jdbc/execute! connection [schema-sql])

    (testing "that we can insert a new contact entity"
      (is (= fixtures.contact/contact
             (database.contact/insert! fixtures.contact/contact connection))))))

(s/deftest by-customer-id-test
  (let [connection (-> (jdbc/get-connection {:jdbcUrl "jdbc:postgres://localhost:5432/postgres?user=postgres&password=postgres"})
                       (jdbc/with-options {:builder-fn rs/as-unqualified-maps}))
        schema-sql (slurp "resources/schema.sql")]

    (jdbc/execute! connection ["DROP TABLE IF EXISTS contact"])
    (jdbc/execute! connection [schema-sql])
    (database.contact/insert! fixtures.contact/contact connection)

    (testing "that we can query contacts by customer id"
      (is (= [fixtures.contact/contact]
             (database.contact/by-customer-id fixtures.customer/customer-id connection))))))
