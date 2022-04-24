(ns porteiro.wire.out.auth
  (:require [schema.core :as s]))

(s/defschema Token
  {:token s/Str})
