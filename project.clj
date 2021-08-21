(defproject microservice-user-management "0.1.0-SNAPSHOT"
  :description "A microservice for users and authentication management"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :plugins [[lein-cloverage "1.2.2"]]

  :dependencies [[io.pedestal/pedestal.service-tools "0.5.7"]
                 [io.pedestal/pedestal.service "0.5.7"]
                 [io.pedestal/pedestal.jetty "0.5.7"]
                 [io.pedestal/pedestal.route "0.5.7"]
                 [cheshire "5.10.0"]
                 [prismatic/schema "1.1.12"]
                 [camel-snake-kebab "0.4.2"]
                 [org.clojure/clojure "1.10.1"]
                 [com.stuartsierra/component "1.0.0"]
                 [com.datomic/datomic-free "0.9.5697"]
                 [nubank/matcher-combinators "3.2.1"]
                 [buddy/buddy-hashers "1.8.1"]]

  :repl-options {:init-ns microservice-user-management.core}

  :test-paths ["test/unit" "test/integration" "test/helpers"]

  :main microservice-user-management.components/start-system!)
