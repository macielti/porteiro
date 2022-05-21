(ns porteiro.diplomat.http-server.contact
  (:require [schema.core :as s]
            [porteiro.controllers.contact :as controllers.contact]))

(s/defn fetch-contacts
  [{{:user-identity/keys [id]} :user-identity
    {:keys [datomic]}          :components}]
  {:status 200
   :body   (controllers.contact/fetch-contacts id (:connection datomic))})
