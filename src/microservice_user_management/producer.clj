(ns microservice-user-management.producer
  (:use [clojure pprint])
  (:require [com.stuartsierra.component :as component]
            [cheshire.core :as json]
            [schema.core :as s])
  (:import (org.apache.kafka.clients.producer KafkaProducer ProducerRecord MockProducer)
           (org.apache.kafka.common.serialization StringSerializer)))

;TODO: Add schemas for topics, message
;TODO: Move it to a separated repository "common-clj"

(defn produce! [{:keys [topic message]} producer]
  (let [record (ProducerRecord. (name topic) (json/encode message))]
    (-> producer
        (.send record)
        .get)))

(s/defn mock-produced-messages [producer :- MockProducer]
  (->> (.history producer)
       seq
       (map (fn [record]
              {:topic (keyword (.topic record))
               :value (json/decode (.value record) true)}))))

(defrecord Producer [config]
  component/Lifecycle

  (start [this]
    (let [bootstrap-server (get-in config [:config :bootstrap-server])
          producer-props   {"value.serializer"  StringSerializer
                            "key.serializer"    StringSerializer
                            "bootstrap.servers" bootstrap-server}
          producer         (KafkaProducer. producer-props)
          mock-producer    (MockProducer. true (StringSerializer.) (StringSerializer.))
          env              (keyword (get-in config [:config :env]))]
      (cond-> this
              true (assoc :producer producer)
              (= env :test) (assoc :producer mock-producer))))

  (stop [this]
    (assoc this :producer nil)))

(defn new-producer []
  (->Producer {}))
