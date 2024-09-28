(ns integration.components-test
  (:require [clojure.test :refer :all]
            [datomic.api :as d]
            [integrant.core :as ig]
            [matcher-combinators.test :refer [match?]]
            [porteiro.db.datomic.customer :as database.customer]
            [porteiro.v2.components :as v2.components]))

(deftest start-system!-test
  (let [system (ig/init v2.components/config-test)
        datomic (-> system :common-clj.integrant-components.datomic/datomic)]

    (testing "that the default admin user was created"
      (is (match? {:customer/hashed-password string?
                   :customer/id              uuid?
                   :customer/roles           [:admin]
                   :customer/username        string?}
                  (database.customer/by-username "admin" (d/db datomic)))))

    (ig/halt! system)))
