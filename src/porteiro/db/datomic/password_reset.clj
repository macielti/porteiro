(ns porteiro.db.datomic.password-reset
  (:require [schema.core :as s]
            [clj-time.coerce :as c]
            [datomic.api :as d]
            [porteiro.wire.datomic.password-reset :as wire.datomic.password-reset])
  (:import (java.util Date)))

(s/defn insert! :- wire.datomic.password-reset/PasswordReset
  [password-reset :- wire.datomic.password-reset/PasswordReset
   datomic]
  (d/transact datomic [password-reset])
  password-reset)

(s/defn valid-password-reset-by-token
  [token :- uuid?
   datomic]
  (let [expiration (c/to-local-date (Date.))
        {:password-reset/keys [created-at] :as result} (-> (d/q '[:find (pull ?password-reset [*])
                                                                  :in $ ?token
                                                                  :where [?password-reset :password-reset/id ?token]
                                                                  [?password-reset :password-reset/state :free]] (d/db datomic) token)
                                                           ffirst)]
    (when (= (c/to-local-date created-at) expiration)
      result)))
;TODO: Make it into a password-reset-request
