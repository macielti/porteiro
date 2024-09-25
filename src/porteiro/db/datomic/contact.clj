(ns porteiro.db.datomic.contact
  (:require [common-clj.integrant-components.datomic :as component.datomic]
            [schema.core :as s]
            [datomic.api :as d]
            [porteiro.models.contact :as models.contact]))

(s/defn insert!
  [contact :- models.contact/Contact
   datomic]
  (-> (component.datomic/transact-and-lookup-entity! :contact/id contact datomic)
      :entity))

(s/defn by-customer-id :- (s/maybe [models.contact/Contact])
  [customer-id :- s/Uuid
   database]
  (some-> (d/q '[:find (pull ?contact [*])
                 :in $ ?customer-id
                 :where [?contact :contact/customer-id ?customer-id]
                 [?contact :contact/status :active]] database customer-id)
          (->> (mapv first))
          (->> (mapv #(dissoc % :db/id)))))
