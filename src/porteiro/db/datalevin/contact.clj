(ns porteiro.db.datalevin.contact
  (:require [datalevin.core :as d]
            [schema.core :as s]
            [porteiro.models.contact :as models.contact]))

(s/defn by-user-id :- (s/maybe [models.contact/Contact])
  [user-id :- s/Uuid
   datalevin-db]
  (some-> (d/q '[:find (pull ?contact [*])
                 :in $ ?user-id
                 :where [?contact :contact/user-id ?user-id]
                 [?contact :contact/status :active]] datalevin-db user-id)
          (->> (mapv first))
          (->> (mapv #(dissoc % :db/id)))))