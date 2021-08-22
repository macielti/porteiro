(ns microservice-user-management.diplomatic.http-in
  (:use [clojure pprint])
  (:require [schema.core :as s]
            [microservice-user-management.controllers.user :as controllers.user]
            [microservice-user-management.controllers.auth :as controllers.auth]))

(s/defn create-user!
  [{user              :json-params
    {:keys [datomic]} :components}]
  {:status 201 :body (controllers.user/create-user! user datomic)})

(s/defn auth
  [{auth                     :json-params
    {:keys [datomic config]} :components}]
  {:status 200 :body (controllers.auth/auth auth config datomic)})