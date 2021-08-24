(ns microservice-user-management.wire.out.healthy
  (:require [schema.core :as s]))

(s/defschema HelthyCheckResult
  "Schema for healthy check result"
  {:isHealthy  s/Bool
   :components [{:component s/Str
                 :isHealthy s/Bool}]})
