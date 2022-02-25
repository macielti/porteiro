(ns porteiro.wire.out.user
  (:require [schema.core :as s]
            [camel-snake-kebab.core :as camel-snake-kebab]
            [porteiro.wire.datomic.user :as wire.datomic.user]))

(def UserRoles (->> wire.datomic.user/roles
                    (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING)
                    (apply s/enum)))

(s/defschema User
  "Schema for user creation request"
  {:id       s/Str
   :username s/Str
   :roles    [UserRoles]
   :email    s/Str})

(s/defschema UserDocument
  {:user User})
