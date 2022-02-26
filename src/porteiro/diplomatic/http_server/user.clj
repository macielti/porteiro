(ns porteiro.diplomatic.http-server.user
  (:require [schema.core :as s]
            [porteiro.controllers.user :as controllers.user]
            [porteiro.adapters.user :as adapters.user]
            [porteiro.adapters.contact :as adapters.contact])
  (:import (java.util UUID)))

(s/defn create-user!
  [{{:keys [email] :as user-wire} :json-params
    {:keys [datomic producer]}    :components}]
  (let [{:user/keys [email] :as user} (adapters.user/wire->internal-user user-wire)
        datomic-user    (adapters.user/internal-user->wire-datomic-user user)
        datomic-contact (adapters.contact/datomic-user-email->internal-email-contact datomic-user email)]
    {:status 201
     :body   (-> (controllers.user/create-user! datomic-user datomic-contact (:connection datomic) producer)
                 (adapters.user/internal-user->wire email))}))

(s/defn add-role!
  [{{wire-user-id :user-id
     wire-role    :role}       :query-params
    {:keys [datomic producer]} :components}]
  (controllers.user/add-role! (UUID/fromString wire-user-id)
                              (adapters.user/wire->internal-role wire-role)
                              (:connection datomic))
  {:status 200})
