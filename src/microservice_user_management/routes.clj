(ns microservice-user-management.routes
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route :as route]
            [io.pedestal.http :as http]))

(defrecord Routes []
  component/Lifecycle

  (start [this]
    (let [common-interceptors [(body-params/body-params)
                               http/json-body]
          routes              (route/expand-routes
                                #{})]
      (assoc this :routes routes)))

  (stop [this]
    (assoc this :routes nil)))

(defn new-routes []
  (->Routes))
