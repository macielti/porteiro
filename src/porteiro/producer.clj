(ns porteiro.producer
  (:use [clojure pprint])
  (:require [com.stuartsierra.component :as component]
            [cheshire.core :as json]
            [environ.core :as environ]
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
          env              (keyword (environ/env :clj-env))]
      (cond-> this
              (not (= env :test)) (assoc :producer (KafkaProducer. producer-props))
              (= env :test) (assoc :producer (MockProducer. true (StringSerializer.) (StringSerializer.))))))

  (stop [this]
    (assoc this :producer nil)))

(defn new-producer []
  (->Producer {}))
