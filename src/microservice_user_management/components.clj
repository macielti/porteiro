(ns microservice-user-management.components
  (:require [com.stuartsierra.component :as component]))

(defn component-system []
  (component/system-map))

(defn start-system! []
  (component/start (component-system)))
