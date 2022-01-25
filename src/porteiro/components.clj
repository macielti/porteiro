(ns porteiro.components
  (:require [com.stuartsierra.component :as component]
            [common-clj.component.config :as component.config]
            [common-clj.component.datomic :as component.datomic]
            [porteiro.server :as server]
            [porteiro.producer :as producer]
            [porteiro.routes :as routes]
            [porteiro.db.datomic.config :as database.config]))

(defn component-system []
  (component/system-map
    :config (component.config/new-config "resources/config.json" :prod)
    :datomic (component/using (component.datomic/new-datomic database.config/schemas) [:config])
    :producer (component/using (producer/new-producer) [:config])
    :routes (component/using (routes/new-routes) [:datomic :producer :config])
    :server (component/using (server/new-server) [:routes :config])))

(defn start-system! []
  (component/start (component-system)))
