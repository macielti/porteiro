(ns porteiro.db.datalevin.password-reset
  (:require [clj-time.coerce :as c]
            [datalevin.core :as d]
            [porteiro.wire.datomic.password-reset :as wire.datomic.password-reset]
            [schema.core :as s])
  (:import (java.util Date)))

(s/defn insert! :- wire.datomic.password-reset/PasswordReset
  [password-reset :- wire.datomic.password-reset/PasswordReset
   datomic]
  (s/validate wire.datomic.password-reset/PasswordReset password-reset)
  (d/transact datomic [password-reset])
  password-reset)

(s/defn valid-password-reset-by-token
  [token :- s/Uuid
   datalevin-db]
  (let [expiration (c/to-local-date (Date.))
        {:password-reset/keys [created-at] :as result} (-> (d/q '[:find (pull ?password-reset [*])
                                                                  :in $ ?token
                                                                  :where [?password-reset :password-reset/id ?token]
                                                                  [?password-reset :password-reset/state :free]] datalevin-db token)
                                                           ffirst
                                                           (dissoc :db/id))]
    (when (= (c/to-local-date created-at) expiration)
      result)))