(ns porteiro.db.datomic.contact
  (:require [schema.core :as s]
            [datomic.api :as d]
            [porteiro.models.contact :as models.contact]))

(s/defn insert!
  [contact :- models.contact/Contact
   datomic]
  (d/transact datomic [contact]))

(s/defn by-user-id :- (s/maybe models.contact/Contact)
  [user-id :- s/Uuid
   datomic]
  (some-> (d/q '[:find (pull ?contact [*])
                 :in $ ?user-id
                 :where [?contact :contact/user-id ?user-id]] (d/db datomic) user-id)
          (->> (map first))
          (->> (map #(dissoc % :db/id)))))
