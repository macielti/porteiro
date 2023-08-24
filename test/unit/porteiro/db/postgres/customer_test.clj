(ns porteiro.db.postgres.customer-test
  (:require [clojure.test :refer :all])
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [fixtures.customer]
            [porteiro.db.postgres.customer :as database.customer]
            [schema.test :as s]))

(s/deftest insert-test
  (let [connection (-> (jdbc/get-connection {:jdbcUrl "jdbc:postgres://localhost:5432/postgres?user=postgres&password=postgres"})
                       (jdbc/with-options {:builder-fn rs/as-unqualified-maps}))
        schema-sql (slurp "resources/schema.sql")]

    (jdbc/execute! connection ["DROP TABLE IF EXISTS customer"])
    (jdbc/execute! connection [schema-sql])

    (testing "that we can insert a customer entity"
      (is (= fixtures.customer/customer
             (database.customer/insert! fixtures.customer/customer connection))))))
