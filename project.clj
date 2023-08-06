(defproject porteiro "0.1.0-SNAPSHOT"
  :description "A microservice for users and authentication management"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :plugins [[lein-cloverage "1.2.3"]
            [lein-environ "1.2.0"]]

  :exclusions [log4j]

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ch.qos.logback/logback-classic "1.4.7"]
                 [io.pedestal/pedestal.service-tools "0.5.10"]
                 [net.clojars.macielti/common-clj "19.30.36"]
                 [camel-snake-kebab "0.4.3"]
                 [danlentz/clj-uuid "0.1.9"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [org.apache.kafka/kafka-clients "3.4.0"]
                 [io.pedestal/pedestal.service "0.5.10"]
                 [com.datomic/datomic-free "0.9.5697"]
                 [io.pedestal/pedestal.jetty "0.5.10"]
                 [io.pedestal/pedestal.route "0.5.10"]
                 [com.stuartsierra/component "1.1.0"]
                 [nubank/matcher-combinators "3.5.0"]
                 [fundingcircle/jackdaw "0.9.9"]
                 [buddy/buddy-hashers "1.8.158"]
                 [prismatic/schema "1.4.1"]
                 [camel-snake-kebab "0.4.3"]
                 [danlentz/clj-uuid "0.1.9"]
                 [buddy/buddy-sign "3.4.333"]
                 [siili/humanize "0.1.1"]
                 [cheshire "5.11.0"]
                 [environ "1.2.0"]]

  :injections [(require 'hashp.core)]

  :resource-paths ["resources"]

  :profiles {:test {:env {:clj-env "test"}}}

  :repl-options {:init-ns porteiro.components}

  :test-paths ["test/unit" "test/integration" "test/helpers"]

  :jvm-opts ^:replace ["--add-opens=java.base/java.nio=ALL-UNNAMED"
                       "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED"
                       "-XX:+UseG1GC"
                       "-Xmx250m"]

  :main porteiro.components/start-system!)
