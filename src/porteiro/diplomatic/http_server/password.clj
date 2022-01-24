(ns porteiro.diplomatic.http-server.password
  (:require [schema.core :as s]
            [porteiro.controllers.password-reset :as controllers.password-reset]
            [porteiro.controllers.user :as controllers.user]
            [porteiro.adapters.user :as adapters.user]))

(s/defn consolidate-reset-password!
  [{password-reset    :json-params
    {:keys [datomic]} :components}]
  (controllers.password-reset/consolidate-password-reset! password-reset datomic)
  {:status 204 :body nil})

(s/defn reset-password!
  [{password-reset             :json-params
    {:keys [producer datomic]} :components}]
  (controllers.user/reset-password! (adapters.user/wire->password-reset-internal password-reset)
                                    producer
                                    datomic)
  {:status 202 :body {:message (str "If you email is on our system, you should "
                                    "receive a password reset link soon")}})

(s/defn update-password!
  [{password-update   :json-params
    {:keys [datomic]} :components
    {:keys [id]}      :user-identity}]
  (controllers.user/update-password! (adapters.user/wire->password-update-internal password-update) id datomic)
  {:status 204 :body nil})