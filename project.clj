(defproject microservice-user-management "0.1.0-SNAPSHOT"
  :description "A microservice for users and authentication management"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :plugins [[lein-cloverage "1.2.2"]]

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.stuartsierra/component "1.0.0"]]

  :test-paths ["test/unit" "test/integration" "test/helpers"]

  :repl-options {:init-ns microservice-user-management.core}
  :main microservice-user-management.core/foo)
