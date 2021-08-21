(ns integration.components-test
  (:use [clojure pprint])
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [microservice-user-management.components :as components])
  (:import (com.stuartsierra.component SystemMap)))

(deftest start-system!-test
  (let [system (components/start-system!)]

    (testing "that the client is able to verify if the server is up with a get request"
      (is (= SystemMap
             (type system))))

    (component/stop system)))
