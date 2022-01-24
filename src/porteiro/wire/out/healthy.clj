(ns microservice-user-management.wire.out.healthy
  (:require [schema.core :as s]))

(s/defschema ComponentHealthyCheckResult
  {:component s/Str
   :isHealthy s/Bool})

(s/defschema HealthyCheckResult
  "Schema for healthy check result"
  {:isHealthy  s/Bool
   :components [ComponentHealthyCheckResult]})
