(ns porteiro.diplomatic.http-server.auth
  (:require [schema.core :as s]
            [porteiro.controllers.auth :as controllers.auth]))

(s/defn auth
        [{auth                     :json-params
          {:keys [datomic producer]} :components}]
  {:status 200 :body (controllers.auth/auth auth producer datomic)})
