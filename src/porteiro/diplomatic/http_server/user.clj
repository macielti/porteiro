(ns porteiro.diplomatic.http-server.user
  (:require [schema.core :as s]
            [porteiro.controllers.user :as controllers.user]
            [porteiro.adapters.user :as adapters.user])
  (:import (java.util UUID)))

(s/defn create-user!
  [{{:keys [email] :as user-wire} :json-params
    {:keys [datomic producer]}    :components}]
  (let [{:user/keys [email] :as user} (adapters.user/wire->internal-user user-wire)
        datomic-user (adapters.user/internal-user->wire-datomic-user user)]
    {:status 201
     :body   (-> (controllers.user/create-user! datomic-user email (:connection datomic) producer)
                 (adapters.user/internal-user->wire email))}))

(s/defn add-role!
  [{{wire-user-id :id}         :path-params
    {wire-role :role}          :query-params
    {:keys [datomic producer]} :components}]
  (controllers.user/add-role! (UUID/fromString wire-user-id)
                              (adapters.user/wire->internal-role wire-role)
                              (:connection datomic))
  {:status 200})
