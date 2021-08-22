(ns microservice-user-management.routes
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route :as route]
            [io.pedestal.http :as http]
            [microservice-user-management.interceptors.common :as interceptors.common]
            [microservice-user-management.interceptors.user :as interceptors.user]
            [microservice-user-management.diplomatic.http-in :as diplomatic.http-in]))

(defrecord Routes [datomic]
  component/Lifecycle

  (start [this]
    (let [common-interceptors [(body-params/body-params)
                               http/json-body
                               interceptors.common/error-handler-interceptor]
          components          {:datomic (:datomic datomic)}
          routes              (route/expand-routes
                                #{["/users" :post (conj common-interceptors
                                                        (interceptors.common/components-interceptor components)
                                                        interceptors.user/username-already-in-use-interceptor
                                                        diplomatic.http-in/create-user!)
                                   :route-name :registry-new-user]})]
      (assoc this :routes routes)))

  (stop [this]
    (assoc this :routes nil)))

(defn new-routes []
  (->Routes {}))
