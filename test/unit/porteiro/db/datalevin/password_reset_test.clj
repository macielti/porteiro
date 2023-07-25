(ns porteiro.db.datalevin.password-reset-test
  (:require [clojure.test :refer :all]
            [datalevin.core :as d]
            [fixtures.password-reset]
            [porteiro.db.datalevin.password-reset :as database.password-reset]
            [schema.test :as s]))

(s/deftest insert-test
  (testing "that we can insert a password reset entity"
    (let [database-uri (datalevin.util/tmp-dir (str "query-or-" (random-uuid)))
          database-connection (d/get-conn database-uri)]

      (database.password-reset/insert! fixtures.password-reset/datalevin-password-reset
                                       database-connection)

      (is (= fixtures.password-reset/datalevin-password-reset
             (database.password-reset/valid-password-reset-by-token fixtures.password-reset/password-reset-id (d/db database-connection)))))))
