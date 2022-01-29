(ns porteiro.diplomatic.http-server.auth
  (:require [schema.core :as s]
            [porteiro.adapters.auth :as adapters.auth]
            [porteiro.controllers.auth :as controllers.auth]
            [taoensso.timbre :as timbre]))

(s/defn authenticate-user!
  [{auth                       :json-params
    {:keys [datomic producer]} :components}]
  {:status 200 :body (controllers.auth/user-authentication! (adapters.auth/wire->internal-user-auth auth)
                                                            producer
                                                            (:connection datomic))})
