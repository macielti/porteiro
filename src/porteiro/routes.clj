(ns porteiro.routes
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route :as route]
            [io.pedestal.http :as http]
            [porteiro.interceptors.common :as interceptors.common]
            [porteiro.interceptors.user :as interceptors.user]
            [porteiro.interceptors.password-reset :as interceptors.password-reset]
            [porteiro.diplomatic.http-server.healthy :as diplomatic.http-server.healthy]
            [porteiro.diplomatic.http-server.auth :as diplomatic.http-server.auth]
            [porteiro.diplomatic.http-server.user :as diplomatic.http-server.user]
            [porteiro.diplomatic.http-server.password :as diplomatic.http-server.password]))

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
                                                         diplomatic.http-server.healthy/healthy-check)
                                   :route-name :healthy-check]
                                  ["/auth" :post (conj common-interceptors
                                                       (interceptors.common/components-interceptor components)
                                                       diplomatic.http-server.auth/authenticate-user!)
                                   :route-name :auth]
                                  ["/user" :post (conj common-interceptors
                                                       (interceptors.common/components-interceptor components)
                                                       interceptors.user/username-already-in-use-interceptor
                                                       diplomatic.http-server.user/create-user!)
                                   :route-name :registry-new-user]
                                  ["/user/password" :put (conj common-interceptors
                                                               (interceptors.common/components-interceptor components)
                                                               interceptors.user/auth-interceptor
                                                               diplomatic.http-server.password/update-password!)
                                   :route-name :update-password]
                                  ["/user/password-reset" :post (conj common-interceptors
                                                                      (interceptors.common/components-interceptor components)
                                                                      diplomatic.http-server.password/reset-password!)
                                   :route-name :reset-password]
                                  ["/user/password-reset" :put (conj common-interceptors
                                                                     (interceptors.common/components-interceptor components)
                                                                     interceptors.password-reset/valid-password-reset-execution-token
                                                                     diplomatic.http-server.password/execute-reset-password!)
                                   :route-name :consolidate-password-reset]})]
      (assoc this :routes routes)))

  (stop [this]
    (assoc this :routes nil)))

(defn new-routes []
  (->Routes {} {} {}))
