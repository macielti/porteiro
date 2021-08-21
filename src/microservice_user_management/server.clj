(ns microservice-user-management.server
  (:require [io.pedestal.http :as http]
            [com.stuartsierra.component :as component]))

(defrecord Server [routes]
  component/Lifecycle
  (start [this]
    (let [service-map {::http/routes (:routes routes)
                       ::http/host   "0.0.0.0"              ;TODO: get this port from config file
                       ::http/port   8888                   ;TODO: get this port from config file
                       ::http/type   :jetty
                       ::http/join?  false}]
      (assoc this :server (http/start (http/create-server service-map)))))
  (stop [this]
    (http/stop (:server this))
    (assoc this :server nil)))

(defn new-server []
  (->Server {}))
