(ns porteiro.wire.out.customer
  (:require [schema.core :as s]))

(s/defschema Customer
  "Schema for user creation request"
  {:id       s/Str
   :username s/Str
   :roles    [s/Str]})

(s/defschema CustomerDocument
  {:customer Customer})
