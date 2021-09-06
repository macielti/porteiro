(ns microservice-user-management.db.datomic.user
  (:use [clojure pprint])
  (:require [schema.core :as s]
            [microservice-user-management.wire.datomic.user :as wire.datomic.user]
            [datomic.api :as d]))

(s/defn insert! :- wire.datomic.user/User
  [user :- wire.datomic.user/User
   datomic]
  (d/transact datomic [user])
  user)

(s/defn by-username :- wire.datomic.user/User
  [username :- s/Str
   datomic]
  (-> (d/q '[:find (pull ?user [:user/id :user/username :user/hashed-password])
             :in $ ?username
             :where [?user :user/username ?username]] (d/db datomic) username)
      ffirst))

(s/defn by-email :- wire.datomic.user/User
  [email :- s/Str
   datomic]
  (-> (d/q '[:find (pull ?user [*])
             :in $ ?email
             :where [?user :user/email ?email]] (d/db datomic) email)
      ffirst))

(s/defn by-id :- wire.datomic.user/User
  [user-id :- s/Uuid
   datomic]
  (-> (d/q '[:find (pull ?e [:user/id :user/username :user/hashed-password])
             :in $ ?user-id
             :where [?e :user/id ?user-id]] (d/db datomic) user-id)
      ffirst))
