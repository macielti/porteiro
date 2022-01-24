(ns porteiro.components
  (:require [com.stuartsierra.component :as component]
            [porteiro.config :as config]
            [porteiro.datomic :as datomic]
            [porteiro.server :as server]
            [porteiro.producer :as producer]
            [porteiro.routes :as routes]))

(defn component-system []
  (component/system-map
    :config (config/new-config)
    :datomic (component/using (datomic/new-datomic) [:config])
    :producer (component/using (producer/new-producer) [:config])
    :routes (component/using (routes/new-routes) [:datomic :producer :config])
    :server (component/using (server/new-server) [:routes :config])))

(defn start-system! []
  (component/start (component-system)))
