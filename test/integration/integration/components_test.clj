(ns integration.components-test
  (:require [clojure.test :refer :all]
            [datalevin.core :as d]
            [matcher-combinators.test :refer [match?]]
            [com.stuartsierra.component :as component]
            [common-clj.component.helper.core :as component.helper]
            [porteiro.components :as components]
            [porteiro.db.datalevin.user :as datalevin.user])
  (:import (com.stuartsierra.component SystemMap)))

(deftest start-system!-test
  (let [system (component/start components/system-test)
        datalevin-connection (component.helper/get-component-content :datalevin system)]

    (testing "that the client is able to verify if the server is up with a get request"
      (is (= SystemMap
             (type system))))

    (testing "that the default admin user was created"
      (is (match? {:user/hashed-password string?
                   :user/id              uuid?
                   :user/roles           [:admin]
                   :user/username        string?}
                  (datalevin.user/by-username "admin" (d/db datalevin-connection)))))

    (component/stop system)))
