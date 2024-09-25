(ns porteiro.db.datomic.customer-test
  (:require [clojure.test :refer :all]
            [porteiro.db.datomic.config :as database.config]
            [porteiro.db.datomic.customer :as database.customer]
            [common-clj.integrant-components.datomic :as component.datomic]
            [fixtures.customer]
            [datomic.api :as d]
            [schema.test :as s]))

(s/deftest insert-test
  (testing "That we can insert a customer"
    (let [database-conn (component.datomic/mocked-datomic database.config/schemas)]
      (is (= fixtures.customer/customer
             (database.customer/insert! fixtures.customer/customer database-conn)))
      (d/release database-conn))))

(deftest by-username-test
  (testing "That we can find a customer by username"
    (let [database-conn (component.datomic/mocked-datomic database.config/schemas)]
      (database.customer/insert! fixtures.customer/customer database-conn)

      (is (= fixtures.customer/customer
             (database.customer/by-username fixtures.customer/customer-username (d/db database-conn))))

      (d/release database-conn))))


