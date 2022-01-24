(ns microservice-user-management.diplomatic.http-server.healthy
  (:require [schema.core :as s]
            [microservice-user-management.controllers.healthy :as controllers.healthy]
            [microservice-user-management.adapters.healthy :as adapters.healthy]))

(s/defn healthy-check
        [{{:keys [datomic config]} :components}]
        (let [healthy-check-result (controllers.healthy/healthy-check datomic config)]
          {:status (adapters.healthy/healthy-check-result->status-code healthy-check-result)
           :body   (adapters.healthy/->wire healthy-check-result)}))
