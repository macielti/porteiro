(ns porteiro.diplomat.http-server.auth
  (:require [schema.core :as s]
            [porteiro.adapters.auth :as adapters.auth]
            [porteiro.controllers.auth :as controllers.auth]))

(s/defn authenticate-customer!
  [{auth                                          :json-params
    {:keys [postgresql rabbitmq-producer config]} :components}]
  {:status 200
   :body   (-> (controllers.auth/customer-authentication! (adapters.auth/wire->internal-customer-auth auth)
                                                          config
                                                          rabbitmq-producer
                                                          postgresql)
               adapters.auth/token->wire)})
