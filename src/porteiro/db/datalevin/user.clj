(ns porteiro.db.datalevin.user
  (:require [datalevin.core :as d]
            [porteiro.wire.datomic.customer :as wire.datomic.user]
            [schema.core :as s]
            [porteiro.models.customer :as models.user]
            [porteiro.models.contact :as models.contact]))

(s/defn insert! :- models.user/Customer
  [user :- models.user/Customer
   datalevin-connection]
  (s/validate models.user/Customer user)
  (d/transact datalevin-connection [user])
  user)

(s/defn insert-user-with-contact! :- models.user/Customer
  [user :- models.user/Customer
   contact :- models.contact/Contact
   datalevin-connection]
  (s/validate models.user/Customer user)
  (s/validate models.contact/Contact contact)
  (d/transact datalevin-connection [user contact])
  user)

(s/defn lookup :- (s/maybe models.user/Customer)
  [user-id :- s/Uuid
   datalevin-database]
  (some-> (d/q '[:find (pull ?user [*])
                 :in $ ?user-id
                 :where [?user :user/id ?user-id]] datalevin-database user-id)
          ffirst
          (dissoc :db/id)))

(s/defn add-role!
  [user-id :- s/Uuid
   role :- wire.datomic.user/UserRoles
   datalevin-connection]
  (s/validate  wire.datomic.user/UserRoles role)
  (d/transact datalevin-connection [[:db/add [:user/id user-id] :user/roles role]]))

(s/defn by-email :- (s/maybe models.user/Customer)
  [email :- s/Str
   datalevin-db]
  (some-> (d/q '[:find (pull ?user [*])
                 :in $ ?email
                 :where [?contact :contact/email ?email]
                 [?contact :contact/status :active]
                 [?contact :contact/user-id ?user-id]
                 [?user :user/id ?user-id]] datalevin-db email)
          ffirst
          (dissoc :db/id)))

(s/defn by-username :- (s/maybe models.user/Customer)
  [username :- s/Str
   datalevin-database]
  (some-> (d/q '[:find (pull ?user [*])
                 :in $ ?username
                 :where [?user :user/username ?username]] datalevin-database username)
          ffirst
          (dissoc :db/id)))
