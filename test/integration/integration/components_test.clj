(ns integration.components-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [porteiro.components :as components])
  (:import (com.stuartsierra.component SystemMap)))

(deftest start-system!-test
  (let [system (component/start components/system-test)]

    (testing "that the client is able to verify if the server is up with a get request"
      (is (= SystemMap
             (type system))))

    (component/stop system)))
