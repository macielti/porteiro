(ns porteiro.diplomat.http-server.contact
  (:require [schema.core :as s]
            [porteiro.controllers.contact :as controllers.contact]))

(s/defn fetch-contacts
  [{{:customer-identity/keys [id]} :customer-identity
    {:keys [datomic]}              :components}]
  {:status 200
   :body   (controllers.contact/fetch-contacts id datomic)})
