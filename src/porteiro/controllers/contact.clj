(ns porteiro.controllers.contact
  (:require [datomic.api :as d]
            [schema.core :as s]
            [porteiro.db.datomic.contact :as database.contact]))

(s/defn fetch-contacts
  [customer-id :- s/Uuid
   datomic]
  (database.contact/by-customer-id customer-id (d/db datomic)))
