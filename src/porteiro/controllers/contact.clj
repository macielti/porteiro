(ns porteiro.controllers.contact
  (:require [schema.core :as s]
            [porteiro.db.datomic.contact :as datomic.contact]))

(s/defn fetch-contacts
  [user-id :- s/Uuid
   datomic]
  (datomic.contact/by-user-id user-id datomic))
