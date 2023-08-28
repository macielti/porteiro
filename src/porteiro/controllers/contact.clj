(ns porteiro.controllers.contact
  (:require [schema.core :as s]
            [porteiro.db.postgres.contact :as database.contact]))

(s/defn fetch-contacts
  [customer-id :- s/Uuid
   database-connection]
  (database.contact/by-customer-id customer-id database-connection))
