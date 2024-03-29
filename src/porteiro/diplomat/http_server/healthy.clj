(ns porteiro.diplomat.http-server.healthy
  (:require [schema.core :as s]
            [porteiro.controllers.healthy :as controllers.healthy]
            [porteiro.adapters.healthy :as adapters.healthy]))

(s/defn healthy-check
  [{{:keys [datalevin config]} :components}]
  (let [healthy-check-result (controllers.healthy/healthy-check datalevin config)]
    {:status (adapters.healthy/healthy-check-result->status-code healthy-check-result)
     :body   (adapters.healthy/->wire healthy-check-result)}))
