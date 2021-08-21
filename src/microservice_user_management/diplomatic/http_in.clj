(ns microservice-user-management.diplomatic.http-in
  (:use [clojure pprint])
  (:require [schema.core :as s]
            [microservice-user-management.controllers.user :as controllers.user]))

(s/defn create-user!
  [{user              :json-params
    {:keys [datomic]} :components}]
  {:status 201 :body (controllers.user/create-user! user datomic)})
