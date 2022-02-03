(ns porteiro.db.datomic.contact
  (:require [schema.core :as s]
            [datomic.api :as d]
            [porteiro.models.contact :as models.contact]))

(s/defn insert!
  [contact :- models.contact/Contact
   datomic]
  (d/transact datomic [contact]))
