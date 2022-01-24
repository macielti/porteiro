(ns microservice-user-management.models.healthy
  (:require [schema.core :as s]))

(s/def ComponentHealthyCheckResult
  {:component  s/Keyword
   :is-healthy s/Bool})

(s/def HealthyCheckResult
  {:is-healthy s/Bool
   :components [ComponentHealthyCheckResult]})
