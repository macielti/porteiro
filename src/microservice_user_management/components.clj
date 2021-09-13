(ns microservice-user-management.components
  (:require [com.stuartsierra.component :as component]
            [microservice-user-management.config :as config]
            [microservice-user-management.datomic :as datomic]
            [microservice-user-management.server :as server]
            [microservice-user-management.producer :as producer]
            [microservice-user-management.routes :as routes]))

(defn component-system []
  (component/system-map
    :config (config/new-config)
    :datomic (component/using (datomic/new-datomic) [:config])
    :producer (component/using (producer/new-producer) [:config])
    :routes (component/using (routes/new-routes) [:datomic :producer :config])
    :server (component/using (server/new-server) [:routes :config])))

(defn start-system! []
  (component/start (component-system)))
