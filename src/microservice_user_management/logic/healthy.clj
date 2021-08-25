(ns microservice-user-management.logic.healthy
  (:require [schema.core :as s]
            [microservice-user-management.models.healthy :as models.healthy]))

(s/defn system-healthy? :- s/Bool
  [dependencies :- {:components [models.healthy/ComponentHealthyCheckResult]}]
  (every? :is-healthy
          (:components dependencies)))
