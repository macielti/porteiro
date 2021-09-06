(ns microservice-user-management.diplomatic.http-server.password-reset
  (:require [schema.core :as s]
            [microservice-user-management.controllers.password-reset :as controllers.password-reset]))

(s/defn consolidate-reset-password!
  [{password-reset    :json-params
    {:keys [datomic]} :components}]
  (controllers.password-reset/consolidate-password-reset! password-reset datomic)
  {:status 204 :body nil})
