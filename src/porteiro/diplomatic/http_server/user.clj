(ns porteiro.diplomatic.http-server.user
  (:require [schema.core :as s]
            [porteiro.controllers.user :as controllers.user]
            [porteiro.controllers.auth :as controllers.auth]
            [porteiro.adapters.user :as adapters.user]
            [porteiro.adapters.contact :as adapters.contact]
            [porteiro.adapters.auth :as adapters.auth])
  (:import (java.util UUID)))

(s/defn create-user!
  [{user-wire                                 :json-params
    {:keys [datomic producer config datomic]} :components}]
  (let [{:user/keys [email username] :as user} (adapters.user/wire->internal-user user-wire)
        datomic-user (adapters.user/internal-user->wire-datomic-user user)
        datomic-contact (adapters.contact/datomic-user-email->internal-email-contact datomic-user email)
        user (controllers.user/create-user! datomic-user datomic-contact (:connection datomic))
        authentication (controllers.auth/user-authentication! (adapters.auth/user-wire->internal-user-auth user-wire)
                                                              config
                                                              producer
                                                              datomic)]
    {:status 201
     :body   nil}))

(s/defn add-role!
  [{{wire-user-id :user-id
     wire-role    :role} :query-params
    {:keys [datomic]}    :components}]
  {:status 200
   :body   (-> (controllers.user/add-role! (UUID/fromString wire-user-id)
                                           (adapters.user/wire->internal-role wire-role)
                                           (:connection datomic))
               adapters.user/internal-user->wire-without-email)})
