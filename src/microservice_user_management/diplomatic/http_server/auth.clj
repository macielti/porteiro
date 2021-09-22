(ns microservice-user-management.diplomatic.http-server.auth
  (:require [schema.core :as s]
            [microservice-user-management.controllers.auth :as controllers.auth]))

(s/defn auth
        [{auth                     :json-params
          {:keys [datomic]} :components}]
  {:status 200 :body (controllers.auth/auth auth datomic)})
