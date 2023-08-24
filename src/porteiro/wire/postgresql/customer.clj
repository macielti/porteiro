(ns porteiro.wire.postgresql.customer
  (:require [schema.core :as s]))

(def base-customer
  {:id                     s/Uuid
   :username               s/Str
   (s/optional-key :roles) s/Any
   :hashed_password        s/Str})

(s/defschema Customer
  base-customer)