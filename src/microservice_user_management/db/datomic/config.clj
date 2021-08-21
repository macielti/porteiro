(ns microservice-user-management.db.datomic.config
  (:require [microservice-user-management.wire.datomic.user :as wire.datomic.user]))

(def schemas (concat [] wire.datomic.user/user))
