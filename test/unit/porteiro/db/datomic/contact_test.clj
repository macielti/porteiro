(ns porteiro.db.datomic.contact-test
  (:require [clojure.test :refer :all]
            [schema.test :as s]
            [matcher-combinators.test :refer [match?]]
            [porteiro.db.datomic.contact :as database.contact]
            [porteiro.db.datomic.config :as database.config]
            [fixtures.user]
            [fixtures.contact]
            [common-clj.component.datomic :as component.datomic]))

(s/deftest insert!-test
  (let [mock-datomic (component.datomic/mocked-datomic database.config/schemas)]
    (testing "that we can insert a contact entity"
      (database.contact/insert! fixtures.contact/datomic-telegram-contact mock-datomic))))

(deftest by-user-id-test
  (let [mock-datomic (component.datomic/mocked-datomic database.config/schemas)]
    (database.contact/insert! fixtures.contact/datomic-telegram-contact mock-datomic)
    (testing "that we can query contact by user-id"
      (is (match? [{:contact/id         uuid?
                    :contact/user-id    fixtures.user/user-id
                    :contact/chat-id    "123456789"
                    :contact/type       :telegram
                    :contact/created-at inst?}]
                  (database.contact/by-user-id fixtures.user/user-id mock-datomic))))))
