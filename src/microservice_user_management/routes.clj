(ns microservice-user-management.routes
  (:use [clojure pprint])
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route :as route]
            [io.pedestal.http :as http]
            [microservice-user-management.interceptors.common :as interceptors.common]
            [microservice-user-management.interceptors.user :as interceptors.user]
            [microservice-user-management.interceptors.password-reset :as interceptors.password-reset]
            [microservice-user-management.diplomatic.http-in :as diplomatic.http-in]
            [microservice-user-management.diplomatic.http-server.password-reset :as diplomatic.http-server.password]))

(defrecord Routes [datomic producer config]
  component/Lifecycle

  (start [this]
    (let [common-interceptors [(body-params/body-params)
                               http/json-body
                               interceptors.common/error-handler-interceptor]
          components          {:datomic  (:datomic datomic)
                               :producer (:producer producer)
                               :config   (:config config)}
          routes              (route/expand-routes
                                #{["/healthy" :get (conj common-interceptors
                                                         (interceptors.common/components-interceptor components)
                                                         diplomatic.http-in/healthy-check)
                                   :route-name :healthy-check]
                                  ["/auth" :post (conj common-interceptors
                                                       (interceptors.common/components-interceptor components)
                                                       diplomatic.http-in/auth)
                                   :route-name :auth]
                                  ["/user" :post (conj common-interceptors
                                                       (interceptors.common/components-interceptor components)
                                                       interceptors.user/username-already-in-use-interceptor
                                                       diplomatic.http-in/create-user!)
                                   :route-name :registry-new-user]
                                  ["/user/password" :put (conj common-interceptors
                                                               (interceptors.common/components-interceptor components)
                                                               interceptors.user/auth-interceptor
                                                               diplomatic.http-in/update-password!)
                                   :route-name :update-password]
                                  ["/user/password-reset" :post (conj common-interceptors
                                                                      (interceptors.common/components-interceptor components)
                                                                      diplomatic.http-in/reset-password!)
                                   :route-name :reset-password]
                                  ["/user/password-reset" :put (conj common-interceptors
                                                                     (interceptors.common/components-interceptor components)
                                                                     interceptors.password-reset/valid-password-reset-consolidation-token
                                                                     diplomatic.http-server.password/consolidate-reset-password!)
                                   :route-name :consolidate-password-reset]})]
      (assoc this :routes routes)))

  (stop [this]
    (assoc this :routes nil)))

(defn new-routes []
  (->Routes {} {} {}))
