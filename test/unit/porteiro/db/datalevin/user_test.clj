(ns porteiro.db.datalevin.user-test
  (:require [clojure.test :refer :all]
            [fixtures.user]
            [fixtures.contact]
            [datalevin.core :as d]
            [datalevin.core :as datalevin]
            [porteiro.db.datalevin.user :as database.user]
            [porteiro.db.datalevin.contact :as database.contact]
            [schema.test :as s]))

(s/deftest insert-test
  (testing "that we can insert a user entity, and lookup for it later"
    (let [database-uri (datalevin.util/tmp-dir (str "query-or-" (random-uuid)))
        database-connection (datalevin/get-conn database-uri)]
    (database.user/insert! fixtures.user/datalevin-user database-connection)
    (is (= fixtures.user/datalevin-user
           (database.user/lookup fixtures.user/user-id (d/db database-connection)))))))

(s/deftest insert-user-with-contact-test
  (testing "that we can insert a user entity along with contact entity in only one transaction"
    (let [database-uri (datalevin.util/tmp-dir (str "query-or-" (random-uuid)))
          database-connection (datalevin/get-conn database-uri)]
      (database.user/insert-user-with-contact! fixtures.user/datalevin-user
                                               fixtures.contact/datalevin-contact
                                               database-connection)
      (is (= fixtures.user/datalevin-user
             (database.user/lookup fixtures.user/user-id (d/db database-connection))))

      (is (= [fixtures.contact/datalevin-contact]
             (database.contact/by-user-id fixtures.user/user-id (d/db database-connection)))))))
