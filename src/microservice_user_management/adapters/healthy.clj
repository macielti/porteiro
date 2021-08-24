(ns microservice-user-management.adapters.healthy
  (:require [schema.core :as s]))

(defn healthy-check-result->status-code
  [healthy-check-result]
  (if (:is-healthy healthy-check-result)
    200
    503))

(defn ^:private component-healthy-check-result->wire
  [{:keys [component is-healthy]}]
  {:component component
   :isHealthy is-healthy})

(s/defn ->wire
  [healthy-check-result]
  )
