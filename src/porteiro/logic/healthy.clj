(ns porteiro.logic.healthy
  (:require [schema.core :as s]
            [porteiro.models.healthy :as models.healthy]))

(s/defn system-healthy? :- s/Bool
  [dependencies :- {:components [models.healthy/ComponentHealthyCheckResult]}]
  (every? :is-healthy
          (:components dependencies)))
