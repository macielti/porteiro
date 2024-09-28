(ns porteiro.diplomat.http-server.auth
  (:require [datomic.api :as d]
            [porteiro.adapters.auth :as adapters.auth]
            [porteiro.controllers.auth :as controllers.auth]
            [schema.core :as s]))

(s/defn authenticate-customer!
  [{auth                              :json-params
    {:keys [datomic producer config]} :components}]
  {:status 200
   :body   (-> (controllers.auth/customer-authentication! (adapters.auth/wire->internal-customer-auth auth)
                                                          config
                                                          producer
                                                          (d/db datomic))
               adapters.auth/token->wire)})
