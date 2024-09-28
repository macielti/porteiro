(ns porteiro.db.datomic.customer-test
  (:require [clojure.test :refer :all]
            [common-clj.integrant-components.datomic :as component.datomic]
            [datomic.api :as d]
            [fixtures.contact]
            [fixtures.customer]
            [porteiro.db.datomic.config :as database.config]
            [porteiro.db.datomic.contact :as database.contact]
            [porteiro.db.datomic.customer :as database.customer]
            [schema.test :as s]))

(s/deftest insert-test
  (testing "That we can insert a customer"
    (let [database-conn (component.datomic/mocked-datomic database.config/schemas)]
      (is (= fixtures.customer/customer
             (database.customer/insert! fixtures.customer/customer database-conn))))))

(s/deftest by-username-test
  (testing "That we can find a customer by username"
    (let [database-conn (component.datomic/mocked-datomic database.config/schemas)]
      (database.customer/insert! fixtures.customer/customer database-conn)

      (is (= fixtures.customer/customer
             (database.customer/by-username fixtures.customer/customer-username (d/db database-conn)))))))

(s/deftest insert-customer-with-contact!-test
  (testing "That we can insert a customer with a contact"
    (let [database-conn (component.datomic/mocked-datomic database.config/schemas)]
      (database.customer/insert-customer-with-contact! fixtures.customer/customer fixtures.contact/contact database-conn)

      (is (= fixtures.customer/customer
             (database.customer/by-username fixtures.customer/customer-username (d/db database-conn))))

      (is (= [fixtures.contact/contact]
             (database.contact/by-customer-id fixtures.customer/customer-id (d/db database-conn)))))))

(s/deftest lookup-test
  (testing "That we can lookup a customer by id"
    (let [database-conn (component.datomic/mocked-datomic database.config/schemas)]
      (database.customer/insert! fixtures.customer/customer database-conn)

      (is (= fixtures.customer/customer
             (database.customer/lookup fixtures.customer/customer-id (d/db database-conn))))

      (is (nil? (database.customer/lookup (random-uuid) (d/db database-conn)))))))

(s/deftest by-email-test
  (testing "That we can find a customer by email"
    (let [database-conn (component.datomic/mocked-datomic database.config/schemas)]
      (database.customer/insert-customer-with-contact! fixtures.customer/customer fixtures.contact/email-contact database-conn)

      (is (= fixtures.customer/customer
             (database.customer/by-email fixtures.contact/email (d/db database-conn)))))))

(s/deftest add-role-test
  (testing "That we can add a role to a customer"
    (let [database-conn (component.datomic/mocked-datomic database.config/schemas)]
      (database.customer/insert! fixtures.customer/customer database-conn)

      (database.customer/add-role! fixtures.customer/customer-id :other database-conn)

      (is (= (assoc fixtures.customer/customer :customer/roles [:admin :other :test])
             (database.customer/lookup fixtures.customer/customer-id (d/db database-conn)))))))
