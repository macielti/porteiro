(ns porteiro.db.datomic.customer
  (:require [common-clj.integrant-components.datomic :as component.datomic]
            [datomic.api :as d]
            [porteiro.models.contact :as models.contact]
            [porteiro.models.customer :as models.customer]
            [schema.core :as s]))

(s/defn insert! :- models.customer/Customer
  [customer :- models.customer/Customer
   datomic]
  (-> (component.datomic/transact-and-lookup-entity! :customer/id customer datomic)
      :entity))

(s/defn insert-customer-with-contact!
  [customer :- models.customer/Customer
   contact :- models.contact/Contact
   datomic]
  (d/transact datomic [customer contact]))

(s/defn add-role!
  [customer-id :- s/Uuid
   role :- s/Keyword
   datomic-connection]
  (d/transact datomic-connection [[:db/add [:customer/id customer-id] :customer/roles role]]))

(s/defn by-username :- (s/maybe models.customer/Customer)
  [username :- s/Str
   database]
  (some-> (d/q '[:find (pull ?customer [*])
                 :in $ ?username
                 :where [?customer :customer/username ?username]] database username)
          ffirst
          (dissoc :db/id)))

(s/defn by-email :- (s/maybe models.customer/Customer)
  [email :- s/Str
   database]
  (some-> (d/q '[:find (pull ?customer [*])
                 :in $ ?email
                 :where [?contact :contact/email ?email]
                 [?contact :contact/status :active]
                 [?contact :contact/customer-id ?customer-id]
                 [?customer :customer/id ?customer-id]] database email)
          ffirst
          (dissoc :db/id)))

(s/defn lookup :- (s/maybe models.customer/Customer)
  [customer-id :- s/Uuid
   database]
  (some-> (d/q '[:find (pull ?e [*])
                 :in $ ?customer-id
                 :where [?e :customer/id ?customer-id]] database customer-id)
          ffirst
          (dissoc :db/id)))
