(ns microservice-user-management.db.datomic.config
  (:require [microservice-user-management.wire.datomic.user :as wire.datomic.user]
            [microservice-user-management.wire.datomic.password-reset :as wire.datomic.password-reset]))

(def schemas (concat []
                     wire.datomic.user/user
                     wire.datomic.password-reset/password-reset))
