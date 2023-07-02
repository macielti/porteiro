(ns porteiro.db.datalevin.user
  (:require [datalevin.core :as d]
            [schema.core :as s]
            [porteiro.models.user :as models.user]))

(s/defn insert! :- models.user/User
  [user :- models.user/User
   datalevin-connection]
  (s/validate models.user/User user)
  (d/transact datalevin-connection [user])
  user)

(s/defn lookup :- (s/maybe models.user/User)
  [user-id :- s/Uuid
   datalevin-db]
  (some-> (d/q '[:find (pull ?user [*])
                 :in $ ?user-id
                 :where [?user :user/id ?user-id]] datalevin-db user-id)
          ffirst
          (dissoc :db/id)))