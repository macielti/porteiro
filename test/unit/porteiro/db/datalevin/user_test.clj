(ns porteiro.db.datalevin.user-test
  (:require [clojure.test :refer :all]
            [fixtures.user]
            [datalevin.core :as d]
            [datalevin.core :as datalevin]
            [porteiro.db.datalevin.user :as database.user]
            [schema.test :as s]))

(s/deftest insert-test
  (let [database-uri (datalevin.util/tmp-dir (str "query-or-" (random-uuid)))
        database-connection (datalevin/get-conn database-uri)]
    (database.user/insert! fixtures.user/datalevin-user database-connection)
    (is (= fixtures.user/datalevin-user
           (database.user/lookup fixtures.user/user-id (d/db database-connection))))))
