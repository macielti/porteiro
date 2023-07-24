(ns porteiro.db.datalevin.contact-test
  (:require [clojure.test :refer :all]
            [datalevin.core :as d]
            [fixtures.contact]
            [porteiro.db.datalevin.contact :as database.contact]
            [schema.test :as s]))

(s/deftest insert-test
  (testing "that we can insert a contact entity"
    (let [database-uri (datalevin.util/tmp-dir (str "query-or-" (random-uuid)))
          database-connection (d/get-conn database-uri)]

      (database.contact/insert! fixtures.contact/datalevin-email-contact
                                database-connection)

      (is (= [fixtures.contact/datalevin-email-contact]
             (database.contact/by-user-id fixtures.user/user-id (d/db database-connection)))))))
