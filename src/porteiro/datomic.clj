(ns microservice-user-management.datomic
  (:require [com.stuartsierra.component :as component]
            [microservice-user-management.db.datomic.config :as datomic.config]
            [datomic.api :as d]))

(defrecord Datomic [config]
  component/Lifecycle

  (start [this]
    (d/create-database (get-in config [:config :datomic-uri]))

    (let [connection (d/connect (get-in config [:config :datomic-uri]))]
      (d/transact connection datomic.config/schemas)
      (assoc this :datomic connection)))

  (stop [this]
    (d/release (:datomic this))
    (assoc this :datomic nil)))

(defn new-datomic []
  (->Datomic {}))
