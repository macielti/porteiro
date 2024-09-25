(ns porteiro.db.datomic.customer
  (:require [porteiro.models.customer :as models.customer]
            [schema.core :as s]
            [datomic.api :as d]
            [porteiro.models.contact :as models.contact]
            [porteiro.wire.datomic.customer :as wire.datomic.user]
            [common-clj.integrant-components.datomic :as component.datomic]))

(s/defn insert! :- models.customer/Customer
  [customer :- models.customer/Customer
   datomic]
  (-> (component.datomic/transact-and-lookup-entity! :customer/id customer datomic)
      :entity))

#_(s/defn insert-user-with-contact! :- models.user/User
    [user :- models.user/User
     contact :- models.contact/Contact
     datomic]
    (d/transact datomic [user contact])
    user)

#_(s/defn add-role!
    [user-id :- s/Uuid
     role :- wire.datomic.user/UserRoles
     datomic-connection]
    (d/transact datomic-connection [[:db/add [:user/id user-id] :user/roles role]]))

(s/defn by-username :- (s/maybe models.customer/Customer)
  [username :- s/Str
   database]
  (some-> (d/q '[:find (pull ?customer [*])
                 :in $ ?username
                 :where [?customer :customer/username ?username]] database username)
          ffirst
          (dissoc :db/id)))

#_(s/defn by-email :- (s/maybe models.user/User)
    [email :- s/Str
     datomic]
    (some-> (d/q '[:find (pull ?user [*])
                   :in $ ?email
                   :where [?contact :contact/email ?email]
                   [?contact :contact/status :active]
                   [?contact :contact/user-id ?user-id]
                   [?user :user/id ?user-id]] (d/db datomic) email)
            ffirst))

#_(s/defn by-id :- (s/maybe models.user/User)
    [user-id :- s/Uuid
     datomic]
    (some-> (d/q '[:find (pull ?e [*])
                   :in $ ?user-id
                   :where [?e :user/id ?user-id]] (d/db datomic) user-id)
            ffirst
            (dissoc :db/id)))
