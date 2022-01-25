(ns porteiro.components
  (:require [com.stuartsierra.component :as component]
            [common-clj.component.config :as component.config]
            [common-clj.component.datomic :as component.datomic]
            [common-clj.component.kafka.producer :as component.producer]
            [common-clj.component.service :as component.service]
            [common-clj.component.routes :as component.routes]
            [porteiro.diplomatic.http-server :as diplomatic.http-server]
            [porteiro.db.datomic.config :as database.config]))

(defn component-system []
  (component/system-map
    :config (component.config/new-config "resources/config.json" :prod)
    :datomic (component/using (component.datomic/new-datomic database.config/schemas) [:config])
    :producer (component/using (component.producer/new-producer) [:config])
    :routes (component/using (component.routes/new-routes diplomatic.http-server/routes) [:datomic :config])
    :service (component/using (component.service/new-service) [:routes :config :datomic])))

(defn start-system! []
  (component/start (component-system)))
