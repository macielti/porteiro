(ns porteiro.db.datomic.user
  (:require [schema.core :as s]
            [datomic.api :as d]
            [porteiro.models.user :as models.user]))

(s/defn insert! :- models.user/User
  [user :- models.user/User
   datomic]
  (d/transact datomic [user])
  user)

(s/defn by-username :- (s/maybe models.user/User)
  [username :- s/Str
   datomic]
  (-> (d/q '[:find (pull ?user [:user/id :user/username :user/email :user/hashed-password])
             :in $ ?username
             :where [?user :user/username ?username]] (d/db datomic) username)
      ffirst))

(s/defn by-email :- models.user/User
  [email :- s/Str
   datomic]
  (-> (d/q '[:find (pull ?user [*])
             :in $ ?email
             :where [?user :user/email ?email]] (d/db datomic) email)
      ffirst))

(s/defn by-id :- models.user/User
  [user-id :- s/Uuid
   datomic]
  (-> (d/q '[:find (pull ?e [:user/id :user/username :user/hashed-password])
             :in $ ?user-id
             :where [?e :user/id ?user-id]] (d/db datomic) user-id)
      ffirst))
