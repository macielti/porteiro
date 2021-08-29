(ns microservice-user-management.producer
  (:use [clojure pprint])
  (:require [com.stuartsierra.component :as component]
            [cheshire.core :as json])
  (:import (org.apache.kafka.clients.producer KafkaProducer ProducerRecord)
           (org.apache.kafka.common.serialization StringSerializer)))

;TODO: Add schemas for topics, message
;TODO: Move it to a separated repository "common-clj"

(defn produce! [{:keys [topic message]} producer]
  (let [record (ProducerRecord. topic (json/encode message))]
    (-> producer
        (.send record)
        .get)))

(defrecord Producer [config]
  component/Lifecycle

  (start [this]
    (let [bootstrap-server (get-in config [:config :bootstrap-server])
          producer-props   {"value.serializer"  StringSerializer
                            "key.serializer"    StringSerializer
                            "bootstrap.servers" bootstrap-server}
          producer         (KafkaProducer. producer-props)]
      (produce! {:topic   "HELLO"
                 :message {:test "ok"}} producer)
      (assoc this :producer producer)))

  (stop [this]
    (assoc this :producer nil)))

(defn new-producer []
  (->Producer {}))
