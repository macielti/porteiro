(ns porteiro.components
  (:require [com.stuartsierra.component :as component]
            [common-clj.component.config :as component.config]
            [common-clj.component.datomic :as component.datomic]
            [common-clj.component.kafka.producer :as component.producer]
            [common-clj.component.kafka.consumer :as component.consumer]
            [common-clj.component.service :as component.service]
            [common-clj.component.routes :as component.routes]
            [porteiro.diplomatic.http-server :as diplomatic.http-server]
            [porteiro.diplomatic.consumer :as diplomatic.consumer]
            [porteiro.db.datomic.config :as database.config]))

(def system
  (component/system-map
    :config (component.config/new-config "resources/config.edn" :prod :edn)
    :datomic (component/using (component.datomic/new-datomic database.config/schemas) [:config])
    :consumer (component/using (component.consumer/new-consumer diplomatic.consumer/topic-consumers) [:config :datomic])
    :producer (component/using (component.producer/new-producer) [:config])
    :routes (component/using (component.routes/new-routes diplomatic.http-server/routes) [:datomic :config])
    :service (component/using (component.service/new-service) [:routes :config :datomic :producer])))

(defn start-system! []
  (component/start system))

(def system-test
  (component/system-map
    :config (component.config/new-config "resources/config.example.edn" :test :edn)
    :datomic (component/using (component.datomic/new-datomic database.config/schemas) [:config])
    :consumer (component/using (component.consumer/new-mock-consumer diplomatic.consumer/topic-consumers) [:config :datomic])
    :producer (component/using (component.producer/new-mock-producer) [:consumer :config])
    :routes (component/using (component.routes/new-routes diplomatic.http-server/routes) [:datomic :config])
    :service (component/using (component.service/new-service) [:routes :config :datomic :producer])))
