(defproject porteiro "0.1.0-SNAPSHOT"
  :description "A microservice for users and authentication management"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :plugins [[lein-cloverage "1.2.2"]
            [lein-environ "1.2.0"]]

  :exclusions [log4j]

  :dependencies [[io.pedestal/pedestal.service-tools "0.5.10"]
                 [net.clojars.macielti/common-clj "5.11.7"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [ch.qos.logback/logback-classic "1.2.10"]
                 [org.apache.kafka/kafka-clients "2.8.0"]
                 [io.pedestal/pedestal.service "0.5.10"]
                 [com.datomic/datomic-free "0.9.5697"]
                 [io.pedestal/pedestal.jetty "0.5.10"]
                 [io.pedestal/pedestal.route "0.5.10"]
                 [com.stuartsierra/component "1.0.0"]
                 [nubank/matcher-combinators "3.3.1"]
                 [org.clojure/tools.logging "1.2.4"]
                 [fundingcircle/jackdaw "0.9.3"]
                 [org.clojure/clojure "1.10.3"]
                 [buddy/buddy-hashers "1.8.1"]
                 [prismatic/schema "1.2.0"]
                 [camel-snake-kebab "0.4.2"]
                 [buddy/buddy-sign "3.4.1"]
                 [siili/humanize "0.1.1"]
                 [cheshire "5.10.1"]
                 [environ "1.2.0"]]

  :resource-paths ["resources"]

  :profiles {:test {:env {:clj-env "test"}}}

  :repl-options {:init-ns porteiro.components}

  :test-paths ["test/unit" "test/integration" "test/helpers"]

  :main porteiro.components/start-system!)
