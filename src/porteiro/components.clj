(ns porteiro.components
  (:require [com.stuartsierra.component :as component]
            [common-clj.component.config :as component.config]
            [common-clj.component.datalevin :as component.datalevin]
            [common-clj.component.rabbitmq.consumer :as component.rabbitmq.consumer]
            [common-clj.component.rabbitmq.producer :as component.rabbitmq.producer]
            [common-clj.component.service :as component.service]
            [common-clj.component.routes :as component.routes]
            [porteiro.admin :as admin]
            [porteiro.diplomat.http-server :as diplomat.http-server]
            [porteiro.diplomat.consumer :as diplomat.consumer]
            [porteiro.db.datalevin.config :as database.config]))

(def system
  (component/system-map
    :config (component.config/new-config "resources/config.edn" :prod :edn)
    :datalevin (component/using (component.datalevin/new-datalevin database.config/schema) [:config])
    :rabbitmq-consumer (component/using (component.rabbitmq.consumer/new-consumer diplomat.consumer/topic-consumers) [:config :datalevin])
    :rabbitmq-producer (component/using (component.rabbitmq.producer/new-producer) [:config])
    :routes (component.routes/new-routes diplomat.http-server/routes)
    :admin (component/using (admin/new-admin) [:datalevin :config])
    :service (component/using (component.service/new-service) [:routes :config :datalevin :rabbitmq-producer])))

(defn start-system! []
  (component/start system))

(def system-test
  (component/system-map
    :config (component.config/new-config "resources/config.example.edn" :test :edn)
    :datalevin (component/using (component.datalevin/new-datalevin database.config/schema) [:config])
    :rabbitmq-consumer (component/using (component.rabbitmq.consumer/new-consumer diplomat.consumer/topic-consumers) [:config :datalevin])
    :rabbitmq-producer (component/using (component.rabbitmq.producer/new-producer) [:config])
    :routes (component/using (component.routes/new-routes diplomat.http-server/routes) [:datalevin :config])
    :admin (component/using (admin/new-admin) [:datalevin :config])
    :service (component/using (component.service/new-service) [:routes :config :datalevin :rabbitmq-producer])))
