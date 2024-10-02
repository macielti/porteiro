(defproject porteiro "0.1.0-SNAPSHOT"

  :description "A microservice for users and authentication management"

  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :plugins [[com.github.clojure-lsp/lein-clojure-lsp "1.4.2"]]

  :exclusions [log4j]

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.postgresql/postgresql "42.7.4"]
                 [net.clojars.macielti/common-clj "30.63.70" :exclusions [datalevin]]
                 [camel-snake-kebab "0.4.3"]
                 [danlentz/clj-uuid "0.1.9"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [nubank/matcher-combinators "3.5.0"]
                 [fundingcircle/jackdaw "0.9.9"]
                 [buddy/buddy-sign "3.4.333"]
                 [buddy/buddy-hashers "1.8.158"]
                 [prismatic/schema "1.4.1"]
                 [siili/humanize "0.1.1"]
                 [cheshire "5.11.0"]]

  :aliases {"clean-ns"     ["clojure-lsp" "clean-ns" "--dry"] ;; check if namespaces are clean
            "format"       ["clojure-lsp" "format" "--dry"] ;; check if namespaces are formatted
            "diagnostics"  ["clojure-lsp" "diagnostics"]    ;; check if project has any diagnostics (clj-kondo findings)
            "lint"         ["do" ["clean-ns"] ["format"] ["diagnostics"]] ;; check all above
            "clean-ns-fix" ["clojure-lsp" "clean-ns"]       ;; Fix namespaces not clean
            "format-fix"   ["clojure-lsp" "format"]         ;; Fix namespaces not formatted
            "lint-fix"     ["do" ["clean-ns-fix"] ["format-fix"]]} ;; Fix both

  :injections [(require 'hashp.core)]

  :resource-paths ["resources" "test/resources/"]

  :repl-options {:init-ns porteiro.v2.components}

  :test-paths ["test/unit" "test/integration" "test/helpers"]

  :src-dirs ["src"]

  :aot :all

  :main porteiro.v2.components)
