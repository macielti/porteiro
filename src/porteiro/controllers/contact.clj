(ns porteiro.controllers.contact
  (:require [datalevin.core :as d]
            [schema.core :as s]
            [porteiro.db.datalevin.contact :as database.contact]))

(s/defn fetch-contacts
  [user-id :- s/Uuid
   datalevin-connection]
  (database.contact/by-user-id user-id (d/db datalevin-connection)))
