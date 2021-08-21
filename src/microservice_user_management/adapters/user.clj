(ns microservice-user-management.adapters.user
  (:require [schema.core :as s]
            [microservice-user-management.wire.in.user :as wire.in.user]
            [microservice-user-management.wire.datomic.user :as wire.datomic.user]
            [buddy.hashers :as hashers])
  (:import (java.util UUID)))

(s/defn wire->internal :- wire.in.user/User
  [user :- wire.in.user/User]
  (s/validate wire.in.user/User user))

(s/defn internal->datomic :- wire.datomic.user/User
  [{:keys [username password email]} :- wire.in.user/User]
  #:user {:id              (UUID/randomUUID)
          :username        username
          :email           email
          :password (hashers/derive password)})
