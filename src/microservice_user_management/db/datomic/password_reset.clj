(ns microservice-user-management.db.datomic.password-reset
  (:require [microservice-user-management.wire.datomic.password-reset :as wire.datomic.password-reset]
            [microservice-user-management.models.password-reset :as models.password]
            [clj-time.coerce :as c]
            [schema.core :as s]
            [datomic.api :as d])
  (:import (java.util Date)))

(s/defn insert! :- wire.datomic.password-reset/PasswordReset
  [password-reset :- wire.datomic.password-reset/PasswordReset
   datomic]
  (d/transact datomic [password-reset])
  password-reset)

(s/defn valid-password-reset-by-token
  [{:keys [token]} :- models.password/PasswordResetConsolidation
   datomic]
  (let [expiration (c/to-local-date (Date.))
        {:password-reset/keys [created-at] :as result} (-> (d/q '[:find (pull ?password-reset [*])
                                                                  :in $ ?token
                                                                  :where [?password-reset :password-reset/id ?token]
                                                                  [?password-reset :password-reset/created-at ?created-at]] (d/db datomic) token)
                                                           ffirst)]
    (when (= (c/to-local-date created-at) expiration)
      result)))
