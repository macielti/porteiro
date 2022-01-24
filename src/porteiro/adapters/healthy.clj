(ns porteiro.adapters.healthy
  (:require [schema.core :as s]
            [porteiro.wire.out.healthy :as wire.out.healthy]
            [porteiro.models.healthy :as models.healthy]))

(s/defn healthy-check-result->status-code :- s/Num
  [healthy-check-result]
  (if (:is-healthy healthy-check-result)
    200
    503))

(s/defn component-healthy-check-result->wire :- wire.out.healthy/ComponentHealthyCheckResult
  [{:keys [component is-healthy]} :- models.healthy/ComponentHealthyCheckResult]
  {:component (name component)
   :isHealthy is-healthy})

(s/defn ->wire :- wire.out.healthy/HealthyCheckResult
  [{:keys [is-healthy components]} :- models.healthy/HealthyCheckResult]
  {:isHealthy  is-healthy
   :components (map component-healthy-check-result->wire components)})
