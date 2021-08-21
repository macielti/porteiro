(ns microservice-user-management.components
  (:require [com.stuartsierra.component :as component]
            [microservice-user-management.config :as config]))

(defn component-system []
  (component/system-map
    :config (config/new-config)))

(defn start-system! []
  (component/start (component-system)))
