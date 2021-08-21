(ns microservice-user-management.components
  (:require [com.stuartsierra.component :as component]
            [microservice-user-management.config :as config]
            [microservice-user-management.datomic :as datomic]))

(defn component-system []
  (component/system-map
    :config (config/new-config)
    :datomic (component/using (datomic/new-datomic) [:config])))

(defn start-system! []
  (component/start (component-system)))
