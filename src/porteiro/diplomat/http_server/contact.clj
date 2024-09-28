(ns porteiro.diplomat.http-server.contact
  (:require [porteiro.controllers.contact :as controllers.contact]
            [schema.core :as s]))

(s/defn fetch-contacts
  [{{:customer-identity/keys [id]} :customer-identity
    {:keys [datomic]}              :components}]
  {:status 200
   :body   (controllers.contact/fetch-contacts id datomic)})
