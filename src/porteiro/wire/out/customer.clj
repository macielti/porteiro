(ns porteiro.wire.out.customer
  (:require [porteiro.models.customer :as models.customer]
            [schema.core :as s]
            [camel-snake-kebab.core :as camel-snake-kebab]
            [porteiro.wire.datomic.user :as wire.datomic.user]))

(def CustomerRoles (->> models.customer/roles
                        (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING)
                        (apply s/enum)))

(s/defschema Customer
  "Schema for user creation request"
  {:id       s/Str
   :username s/Str
   :roles    [CustomerRoles]})

(s/defschema CustomerDocument
  {:customer Customer})
