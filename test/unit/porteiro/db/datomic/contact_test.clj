(ns porteiro.db.datomic.contact-test
  (:require [clojure.test :refer :all]
            [common-clj.integrant-components.datomic :as component.datomic]
            [datomic.api :as d]
            [fixtures.contact]
            [matcher-combinators.test :refer [match?]]
            [porteiro.db.datomic.config :as database.config]
            [porteiro.db.datomic.contact :as database.contact]
            [schema.test :as s]))

(s/deftest insert-test
  (testing "That we can insert a contact"
    (let [datomic (component.datomic/mocked-datomic database.config/schemas)]
      (is (= fixtures.contact/contact
             (database.contact/insert! fixtures.contact/contact datomic))))))

(s/deftest by-customer-id-test
  (testing "That we can find a contact by customer-id"
    (let [datomic (component.datomic/mocked-datomic database.config/schemas)]
      (database.contact/insert! fixtures.contact/contact datomic)
      (is (match? [fixtures.contact/contact]
                  (database.contact/by-customer-id fixtures.customer/customer-id (d/db datomic)))))))

(s/deftest by-email-test
  (testing "That we can find a contact by email"
    (let [datomic (component.datomic/mocked-datomic database.config/schemas)]
      (database.contact/insert! fixtures.contact/email-contact datomic)
      (is (match? [fixtures.contact/email-contact]
                  (database.contact/by-email fixtures.contact/email (d/db datomic)))))))
