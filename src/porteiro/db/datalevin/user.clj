(ns porteiro.db.datalevin.user
  (:require [datalevin.core :as d]
            [porteiro.wire.datomic.user :as wire.datomic.user]
            [schema.core :as s]
            [porteiro.models.user :as models.user]
            [porteiro.models.contact :as models.contact]))

(s/defn insert! :- models.user/User
  [user :- models.user/User
   datalevin-connection]
  (s/validate models.user/User user)
  (d/transact datalevin-connection [user])
  user)

(s/defn insert-user-with-contact! :- models.user/User
  [user :- models.user/User
   contact :- models.contact/Contact
   datalevin-connection]
  (s/validate models.user/User user)
  (s/validate models.contact/Contact contact)
  (d/transact datalevin-connection [user contact])
  user)

(s/defn lookup :- (s/maybe models.user/User)
  [user-id :- s/Uuid
   datalevin-db]
  (some-> (d/q '[:find (pull ?user [*])
                 :in $ ?user-id
                 :where [?user :user/id ?user-id]] datalevin-db user-id)
          ffirst
          (dissoc :db/id)))

(s/defn add-role!
  [user-id :- s/Uuid
   role :- wire.datomic.user/UserRoles
   datalevin-connection]
  (s/validate  wire.datomic.user/UserRoles role)
  (d/transact datalevin-connection [[:db/add [:user/id user-id] :user/roles role]]))
