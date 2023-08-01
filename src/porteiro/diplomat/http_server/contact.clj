(ns porteiro.diplomat.http-server.contact
  (:require [schema.core :as s]
            [porteiro.controllers.contact :as controllers.contact]))

(s/defn fetch-contacts
  [{{:user-identity/keys [id]} :user-identity
    {:keys [datalevin]}          :components}]
  {:status 200
   :body   (controllers.contact/fetch-contacts id datalevin)})
