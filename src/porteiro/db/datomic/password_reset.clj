(ns porteiro.db.datomic.password-reset
  (:require [clj-time.coerce :as c]
            [common-clj.integrant-components.datomic :as component.datomic]
            [datomic.api :as d]
            [porteiro.wire.datomic.password-reset :as wire.datomic.password-reset]
            [schema.core :as s])
  (:import (java.util Date)))

(s/defn insert! :- wire.datomic.password-reset/PasswordReset
  [password-reset :- wire.datomic.password-reset/PasswordReset
   datomic]
  (-> (component.datomic/transact-and-lookup-entity! :password-reset/id password-reset datomic)
      :entity)
  (d/transact datomic [password-reset])
  password-reset)

(s/defn valid-password-reset-by-token
  [token :- s/Uuid
   database]
  (let [expiration (c/to-local-date (Date.))
        {:password-reset/keys [created-at] :as result} (-> (d/q '[:find (pull ?password-reset [*])
                                                                  :in $ ?token
                                                                  :where [?password-reset :password-reset/id ?token]
                                                                  [?password-reset :password-reset/state :free]] database token)
                                                           ffirst
                                                           (dissoc :db/id))]
    (when (= (c/to-local-date created-at) expiration)
      result)))

(s/defn set-as-used! :- wire.datomic.password-reset/PasswordReset
  [password-reset-id :- s/Uuid
   datomic]
  (-> (component.datomic/transact-and-lookup-entity! :password-reset/id {:password-reset/id    password-reset-id
                                                                         :password-reset/state :used} datomic)
      :entity))
