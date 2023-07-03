(ns porteiro.db.datomic.user
  (:require [schema.core :as s]
            [datomic.api :as d]
            [porteiro.models.user :as models.user]
            [porteiro.models.contact :as models.contact]
            [porteiro.wire.datomic.user :as wire.datomic.user]))

(s/defn insert! :- models.user/User
  [user :- models.user/User
   datomic]
  (d/transact datomic [user])
  user)

(s/defn insert-user-with-contact! :- models.user/User
  [user :- models.user/User
   contact :- models.contact/Contact
   datomic]
  (d/transact datomic [user contact])
  user)

(s/defn add-role!
  [user-id :- s/Uuid
   role :- wire.datomic.user/UserRoles
   datomic-connection]
  (d/transact datomic-connection [[:db/add [:user/id user-id] :user/roles role]]))

(s/defn by-username :- (s/maybe models.user/User)
  [username :- s/Str
   datomic]
  (some-> (d/q '[:find (pull ?user [*])
                 :in $ ?username
                 :where [?user :user/username ?username]] (d/db datomic) username)
          ffirst
          (dissoc :db/id)))

(s/defn by-email :- (s/maybe models.user/User)
  [email :- s/Str
   datomic]
  (some-> (d/q '[:find (pull ?user [*])
                 :in $ ?email
                 :where [?contact :contact/email ?email]
                 [?contact :contact/status :active]
                 [?contact :contact/user-id ?user-id]
                 [?user :user/id ?user-id]] (d/db datomic) email)
          ffirst))

(s/defn by-id :- (s/maybe models.user/User)
  [user-id :- s/Uuid
   datomic]
  (some-> (d/q '[:find (pull ?e [*])
                 :in $ ?user-id
                 :where [?e :user/id ?user-id]] (d/db datomic) user-id)
          ffirst
          (dissoc :db/id)))
