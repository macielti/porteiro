(ns porteiro.db.postgres.password-reset-test
  (:require [clojure.test :refer :all]
            [porteiro.db.postgres.password-reset :as database.password-reset]
            [schema.test :as s]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [fixtures.password-reset]))

(s/deftest insert-and-lookup-test
  (let [connection (-> (jdbc/get-connection {:jdbcUrl "jdbc:postgres://localhost:5432/postgres?user=postgres&password=postgres"})
                       (jdbc/with-options {:builder-fn rs/as-unqualified-maps}))
        schema-sql (slurp "resources/schema.sql")]

    (jdbc/execute! connection ["DROP TABLE IF EXISTS password_reset"])
    (jdbc/execute! connection [schema-sql])

    (testing "that we can insert a password-reset entity"
      (is (= fixtures.password-reset/password-reset
             (database.password-reset/insert! fixtures.password-reset/password-reset connection))))

    (testing "that we can lookup password-reset entity by id"
      (is (= fixtures.password-reset/password-reset
             (database.password-reset/lookup fixtures.password-reset/password-reset-id connection))))))
