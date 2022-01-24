(ns microservice-user-management.db.datomic.session
  (:use [clojure pprint])
  (:require [schema.core :as s]
            [microservice-user-management.wire.datomic.session :as wire.datomic.session]
            [datomic.api :as d]))

(s/defn insert! :- wire.datomic.session/Session
  [session :- wire.datomic.session/Session
   datomic]
  (d/transact datomic [session])
  session)

(s/defn valid-session-by-user-id :- wire.datomic.session/Session
  [user-id
   datomic]
  (-> (d/q '[:find (pull ?session [*])
             :in $ ?user-id
             :where [?session :session/user-id ?user-id]
             [?session :session/valid? true]] (d/db datomic) user-id)
      ffirst))
