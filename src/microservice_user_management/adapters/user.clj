(ns microservice-user-management.adapters.user
  (:require [schema.core :as s]
            [humanize.schema :as h]
            [microservice-user-management.wire.in.user :as wire.in.user]
            [microservice-user-management.wire.datomic.user :as wire.datomic.user]
            [microservice-user-management.wire.out.user :as wire.out.user]
            [buddy.hashers :as hashers])
  (:import (java.util UUID)
           (clojure.lang ExceptionInfo)))

(s/defn wire->internal :- wire.in.user/User
  [user :- wire.in.user/User]
  (try
    (s/validate wire.in.user/User user)
    (catch ExceptionInfo e
      (if (= (-> e ex-data :type)
             :schema.core/error)
        (throw (ex-info "Schema error"
                        {:status 422
                         :cause  (get-in (h/ex->err e) [:unknown :error])}))))))

(s/defn internal->datomic :- wire.datomic.user/User
  [{:keys [username password email]} :- wire.in.user/User]
  #:user {:id       (UUID/randomUUID)
          :username username
          :email    email
          :password (hashers/derive password)})

(s/defn datomic->wire :- wire.out.user/User
  [{:user/keys [id username email]} :- wire.datomic.user/User]
  {:id       (str id)
   :username username
   :email    email})
