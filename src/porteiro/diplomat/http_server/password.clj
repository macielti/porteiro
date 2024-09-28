(ns porteiro.diplomat.http-server.password
  (:require [porteiro.adapters.customer :as adapters.user]
            [porteiro.adapters.password-reset :as adapters.password-reset]
            [porteiro.controllers.customer :as controllers.customer]
            [porteiro.controllers.password-reset :as controllers.password-reset]
            [schema.core :as s]))

(s/defn reset-password!
  [{password-reset    :json-params
    {:keys [datomic]} :components}]
  (controllers.password-reset/execute-password-reset!
   (adapters.password-reset/wire->password-reset-execution-internal password-reset)
   datomic)
  {:status 204 :body nil})

(s/defn request-reset-password!
  [{password-reset             :json-params
    {:keys [producer datomic]} :components}]
  (controllers.customer/reset-password! (adapters.user/wire->password-reset-internal password-reset)
                                        producer
                                        datomic)
  {:status 202 :body {:message (str "If the email is correct, you should "
                                    "receive a password reset link soon")}})

(s/defn update-password!
  [{password-update                :json-params
    {:customer-identity/keys [id]} :customer-identity
    {:keys [datomic]}              :components}]
  (controllers.customer/update-password! (adapters.user/wire->password-update-internal password-update)
                                         id
                                         datomic)
  {:status 204 :body nil})
