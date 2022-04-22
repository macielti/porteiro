(ns porteiro.wire.out.auth
  (:require [schema.core :as s]))

(s/defschema AuthenticationResult
  {:token s/Str})
