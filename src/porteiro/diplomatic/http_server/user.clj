(ns porteiro.diplomatic.http-server.user
  (:require [schema.core :as s]
            [porteiro.controllers.user :as controllers.user]))

(s/defn create-user!
  [{user              :json-params
    {:keys [datomic]} :components}]
  {:status 201 :body (controllers.user/create-user! user (:connection datomic))})
