(ns microservice-user-management.controllers.user
  (:use [clojure pprint])
  (:require [schema.core :as s]
            [microservice-user-management.wire.datomic.user :as wire.datomic.user]
            [microservice-user-management.wire.in.user :as wire.in.user]
            [microservice-user-management.adapters.user :as adapters.user]
            [microservice-user-management.db.datomic.user :as datomic.user]
            [microservice-user-management.models.user :as models.user]
            [buddy.hashers :as hashers]))

(s/defn create-user! :- wire.datomic.user/User
  [user :- wire.in.user/User
   datomic]
  (-> user
      adapters.user/wire->create-user-internal
      adapters.user/internal->create-user-datomic
      (datomic.user/insert! datomic)
      adapters.user/datomic->wire))

(s/defn update-password!
  [{:keys [old-password new-password] :as password-update} :- models.user/PasswordUpdate
   user-id :- s/Uuid
   datomic]
  (let [{:user/keys [hashed-password] :as user-datomic} (datomic.user/by-id user-id datomic)]
    (if (and user-datomic
             (:valid (hashers/verify old-password hashed-password)))
      (-> (assoc user-datomic :user/hashed-password (hashers/derive new-password))
          (datomic.user/insert! datomic))
      (throw (ex-info "Incorrect old password"
                      {:status 403
                       :reason "The old password you have entered is incorrect"})))))
