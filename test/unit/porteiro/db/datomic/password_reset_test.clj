(ns porteiro.db.datomic.password-reset-test
  (:require [clojure.test :refer :all]
            [common-clj.integrant-components.datomic :as component.datomic]
            [datomic.api :as d]
            [fixtures.password-reset]
            [porteiro.db.datomic.config :as database.config]
            [porteiro.db.datomic.password-reset :as database.password-reset]
            [schema.test :as s]))

(s/deftest insert!-test
  (testing "That we can insert a password-reset"
    (let [database-conn (component.datomic/mocked-datomic database.config/schemas)]
      (is (= fixtures.password-reset/password-reset
             (database.password-reset/insert! fixtures.password-reset/password-reset database-conn)))
      (d/release database-conn))))

(s/deftest valid-password-reset-by-token-test
  (testing "That we can insert a password-reset"
    (let [database-conn (component.datomic/mocked-datomic database.config/schemas)]

      (database.password-reset/insert! fixtures.password-reset/password-reset database-conn)

      (is (= fixtures.password-reset/password-reset
             (database.password-reset/valid-password-reset-by-token fixtures.password-reset/password-reset-id (d/db database-conn))))
      (d/release database-conn))))
