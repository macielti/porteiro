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

(s/defn lookup :- (s/maybe wire.datomic.password-reset/PasswordReset)
  [password-reset-id :- s/Uuid
   datalevin-database]
  (some-> (d/q '[:find (pull ?password-reset [*])
                 :in $ ?password-reset-id
                 :where [?password-reset :password-reset/id ?password-reset-id]] datalevin-database password-reset-id)
          ffirst
          (dissoc :db/id)))

(s/defn set-as-used!
  [password-reset-id :- s/Uuid
   datalevin-connection]
  (-> (d/entity (d/db datalevin-connection) [:password-reset/id password-reset-id])
      (assoc :password-reset/state :used)
      (->> (conj [])
           (d/transact datalevin-connection))))

(s/defn valid-password-reset-by-token
  [token :- s/Uuid
   datalevin-database]
  (let [expiration (c/to-local-date (Date.))
        {:password-reset/keys [created-at] :as result} (-> (d/q '[:find (pull ?password-reset [*])
                                                                  :in $ ?token
                                                                  :where [?password-reset :password-reset/id ?token]
                                                                  [?password-reset :password-reset/state :free]] datalevin-database token)
                                                           ffirst
                                                           (dissoc :db/id))]
    (when (= (c/to-local-date created-at) expiration)
      result)))
