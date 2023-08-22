(ns porteiro.diplomat.http-server.auth
  (:require [schema.core :as s]
            [porteiro.adapters.auth :as adapters.auth]
            [porteiro.controllers.auth :as controllers.auth]))

(s/defn authenticate-user!
  [{auth                                         :json-params
    {:keys [datalevin rabbitmq-producer config]} :components}]
  (Thread/sleep 5000)
  {:status 200
   :body   (-> (controllers.auth/user-authentication! (adapters.auth/wire->internal-user-auth auth)
                                                      config
                                                      rabbitmq-producer
                                                      datalevin)
               adapters.auth/token->wire)})
