#_(ns porteiro.db.datomic.user-test
  (:require [clojure.test :refer :all]
            [schema.test :as s]
            [datomic.api :as d]
            [matcher-combinators.test :refer [match?]]
            [fixtures.user]
            [porteiro.db.datomic.user :as database.user]
            [common-clj.component.datomic :as component.datomic]
            [porteiro.db.datomic.config :as database.config]))


#_(s/deftest insert!-test
  (let [mock-datomic (component.datomic/mocked-datomic database.config/schemas)]
    (testing "that we can insert a contact entity"
      (database.user/insert! fixtures.user/datomic-user mock-datomic))
    (d/release mock-datomic)))

#_(deftest add-role!-test
  (let [mock-datomic (component.datomic/mocked-datomic database.config/schemas)]
    (database.user/insert! fixtures.user/datomic-user mock-datomic)
    (database.user/add-role! fixtures.user/user-id :admin mock-datomic)
    (testing "that we can insert a contact entity"
      (is (match? {:user/roles [:admin]}
                  (database.user/by-id fixtures.user/user-id mock-datomic))))
    (d/release mock-datomic)))

#_(deftest by-id-test
  (let [mock-datomic (component.datomic/mocked-datomic database.config/schemas)]
    (database.user/insert! fixtures.user/datomic-user mock-datomic)
    (testing "that we can insert a contact entity"
      (is (match? fixtures.user/datomic-user
                  (database.user/by-id fixtures.user/user-id mock-datomic))))
    (d/release mock-datomic)))
