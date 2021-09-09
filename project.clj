(defproject microservice-user-management "0.1.0-SNAPSHOT"
  :description "A microservice for users and authentication management"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :plugins [[lein-cloverage "1.2.2"]
            [lein-environ "1.2.0"]]


  :dependencies [[io.pedestal/pedestal.service-tools "0.5.7"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [org.apache.kafka/kafka-clients "2.8.0"]
                 [io.pedestal/pedestal.service "0.5.7"]
                 [com.datomic/datomic-free "0.9.5697"]
                 [io.pedestal/pedestal.jetty "0.5.7"]
                 [io.pedestal/pedestal.route "0.5.7"]
                 [com.stuartsierra/component "1.0.0"]
                 [nubank/matcher-combinators "3.2.1"]
                 [org.clojure/tools.logging "1.1.0"]
                 [fundingcircle/jackdaw "0.8.0"]
                 [org.clojure/clojure "1.10.1"]
                 [buddy/buddy-hashers "1.8.1"]
                 [prismatic/schema "1.1.12"]
                 [camel-snake-kebab "0.4.2"]
                 [buddy/buddy-sign "3.4.1"]
                 [siili/humanize "0.1.1"]
                 [cheshire "5.10.0"]
                 [environ "1.2.0"]]

  :resource-paths ["resources"]

  :profiles {:test {:env {:clj-env "test"}}}

  :repl-options {:init-ns microservice-user-management.components}

  :test-paths ["test/unit" "test/integration" "test/helpers"]

  :main microservice-user-management.components/start-system!)
