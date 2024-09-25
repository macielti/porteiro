(ns porteiro.db.datomic.contact-test
  (:require [clojure.test :refer :all]
            [datomic.api :as d]
            [matcher-combinators.matchers :as matchers]
            [schema.test :as s]
            [matcher-combinators.test :refer [match?]]
            [porteiro.db.datomic.contact :as database.contact]
            [porteiro.db.datomic.config :as database.config]
            [fixtures.contact]
            [common-clj.integrant-components.datomic :as component.datomic]))

(s/deftest insert-test
  (testing "That we can insert a contact"
    (let [database-conn (component.datomic/mocked-datomic database.config/schemas)]
      (is (= fixtures.contact/contact
             (database.contact/insert! fixtures.contact/contact database-conn)))
      (d/release database-conn))))

(s/deftest by-customer-id-test
  (testing "That we can find a contact by customer-id"
    (let [database-conn (component.datomic/mocked-datomic database.config/schemas)]
      (database.contact/insert! fixtures.contact/contact database-conn)
      (is (match? [fixtures.contact/contact]
                  (database.contact/by-customer-id fixtures.customer/customer-id database-conn)))
      (d/release database-conn))))


