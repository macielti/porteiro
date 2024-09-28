(ns porteiro.controllers.contact
  (:require [datomic.api :as d]
            [porteiro.db.datomic.contact :as database.contact]
            [schema.core :as s]))

(s/defn fetch-contacts
  [customer-id :- s/Uuid
   datomic]
  (database.contact/by-customer-id customer-id (d/db datomic)))
