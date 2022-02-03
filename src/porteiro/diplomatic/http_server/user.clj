(ns porteiro.diplomatic.http-server.user
  (:require [schema.core :as s]
            [porteiro.controllers.user :as controllers.user]
            [porteiro.adapters.user :as adapters.user]
            [porteiro.adapters.contact :as adapters.contact]))

(s/defn create-user!
  [{{:keys [email] :as user-wire} :json-params
    {:keys [datomic producer]}    :components}]
  (let [{:user/keys [email] :as user} (adapters.user/wire->internal-user user-wire)
        datomic-user (adapters.user/internal-user->wire-datomic-user user)]
    {:status 201 :body (-> (controllers.user/create-user! datomic-user email (:connection datomic) producer)
                           adapters.user/internal-user->wire)}))
