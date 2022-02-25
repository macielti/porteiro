(ns porteiro.diplomatic.http-server.auth
  (:require [schema.core :as s]
            [porteiro.adapters.auth :as adapters.auth]
            [porteiro.controllers.auth :as controllers.auth]))

(s/defn authenticate-user!
  [{auth                              :json-params
    {:keys [datomic producer config]} :components}]
  {:status 200 :body (controllers.auth/user-authentication! (adapters.auth/wire->internal-user-auth auth)
                                                            config
                                                            producer
                                                            (:connection datomic))})
