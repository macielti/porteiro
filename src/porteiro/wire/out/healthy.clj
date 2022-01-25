(ns porteiro.wire.out.healthy
  (:require [schema.core :as s]))

(s/defschema ComponentHealthyCheckResult
  {:component s/Str
   :isHealth  s/Bool})

(s/defschema HealthyCheckResult
  "Schema for healthy check result"
  {:isHealth   s/Bool
   :components [ComponentHealthyCheckResult]})
