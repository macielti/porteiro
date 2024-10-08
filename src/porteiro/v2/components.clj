(ns porteiro.v2.components
  (:require [common-clj.integrant-components.config]
            [common-clj.integrant-components.datomic]
            [common-clj.integrant-components.prometheus]
            [common-clj.integrant-components.routes]
            [common-clj.integrant-components.service]
            [common-clj.integrant-components.sqs-consumer]
            [common-clj.integrant-components.sqs-producer]
            [common-clj.integrant-components.aws-auth]
            [integrant.core :as ig]
            [porteiro.db.datomic.config :as database.config]
            [porteiro.diplomat.consumer :as diplomat.consumer]
            [porteiro.diplomat.http-server :as diplomat.http-server]
            [porteiro.v2.admin]
            [taoensso.timbre :as timbre])
  (:gen-class))

(def config
  {:common-clj.integrant-components.config/config             {:path "resources/config.edn"
                                                               :env  :prod}
   :common-clj.integrant-components.aws-auth/aws-auth         {:components {:config (ig/ref :common-clj.integrant-components.config/config)}}
   :common-clj.integrant-components.datomic/datomic           {:schemas    database.config/schemas
                                                               :components {:config (ig/ref :common-clj.integrant-components.config/config)}}
   :common-clj.integrant-components.sqs-producer/sqs-producer {:components {:config (ig/ref :common-clj.integrant-components.config/config)}}
   :common-clj.integrant-components.sqs-consumer/sqs-consumer {:consumers  diplomat.consumer/consumers
                                                               :components {:config  (ig/ref :common-clj.integrant-components.config/config)
                                                                            :datomic (ig/ref :common-clj.integrant-components.datomic/datomic)}}
   :common-clj.integrant-components.routes/routes             {:routes diplomat.http-server/routes}
   :common-clj.integrant-components.prometheus/prometheus     {:metrics []}
   :porteiro.v2.admin/admin                                   {:components {:config  (ig/ref :common-clj.integrant-components.config/config)
                                                                            :datomic (ig/ref :common-clj.integrant-components.datomic/datomic)}}
   :common-clj.integrant-components.service/service           {:components {:prometheus (ig/ref :common-clj.integrant-components.prometheus/prometheus)
                                                                            :config     (ig/ref :common-clj.integrant-components.config/config)
                                                                            :routes     (ig/ref :common-clj.integrant-components.routes/routes)
                                                                            :datomic    (ig/ref :common-clj.integrant-components.datomic/datomic)
                                                                            :producer   (ig/ref :common-clj.integrant-components.sqs-producer/sqs-producer)}}})

(defn start-system! []
  (timbre/set-min-level! :info)
  (ig/init config))

(def -main start-system!)

(def config-test
  (-> config
      (assoc :common-clj.integrant-components.config/config {:path "resources/config.example.edn"
                                                             :env  :test})))
