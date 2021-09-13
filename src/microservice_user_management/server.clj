(ns microservice-user-management.server
  (:use [clojure pprint])
  (:require [io.pedestal.http :as http]
            [com.stuartsierra.component :as component]))

(defrecord Server [routes config]
  component/Lifecycle
  (start [this]
    (let [{{{:keys [host port]} :server} :config} config
          service-map {::http/routes (:routes routes)
                       ::http/host   host
                       ::http/port   port
                       ::http/type   :jetty
                       ::http/join?  false}]
      (assoc this :server (http/start (http/create-server service-map)))))
  (stop [this]
    (http/stop (:server this))
    (assoc this :server nil)))

(defn new-server []
  (->Server {} {}))
