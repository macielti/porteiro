(ns microservice-user-management.diplomatic.http-in
  (:use [clojure pprint])
  (:require [schema.core :as s]
            [microservice-user-management.controllers.user :as controllers.user]
            [microservice-user-management.controllers.auth :as controllers.auth]
            [microservice-user-management.controllers.healthy :as controllers.healthy]
            [microservice-user-management.adapters.healthy :as adapters.healthy]
            [microservice-user-management.adapters.user :as adapters.user]))

(s/defn create-user!
  [{user              :json-params
    {:keys [datomic]} :components}]
  {:status 201 :body (controllers.user/create-user! user datomic)})

(s/defn update-password!
  [{password-update   :json-params
    {:keys [datomic]} :components
    {:keys [id]}      :user-identity}]
  {:status 204 :body (controllers.user/update-password!
                       (adapters.user/wire->password-update-internal password-update)
                       id
                       datomic)})

(s/defn auth
  [{auth                     :json-params
    {:keys [datomic config]} :components}]
  {:status 200 :body (controllers.auth/auth auth config datomic)})

(s/defn healthy-check
  [{{:keys [datomic config]} :components}]
  (let [healthy-check-result (controllers.healthy/healthy-check datomic config)]
    {:status (adapters.healthy/healthy-check-result->status-code healthy-check-result)
     :body   (adapters.healthy/->wire healthy-check-result)}))
