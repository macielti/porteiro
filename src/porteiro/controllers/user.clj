(ns porteiro.controllers.user
  (:require [schema.core :as s]
            [porteiro.wire.in.user :as wire.in.user]
            [porteiro.wire.out.user :as wire.out.user]
            [porteiro.adapters.user :as adapters.user]
            [porteiro.db.datomic.user :as datomic.user]
            [porteiro.models.user :as models.user]
            [porteiro.db.datomic.password-reset :as datomic.password-reset]
            [porteiro.diplomatic.producer :as diplomatic.producer]
            [buddy.hashers :as hashers]))

;TODO: Refact this to receive a internal model
(s/defn create-user! :- wire.out.user/User
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

(s/defn reset-password!                                     ;TODO: Refactor this operation name to request-password-solicitation
  [{:keys [email] :as password-reset} :- wire.in.user/PasswordReset
   producer
   datomic]
  (let [{:user/keys [id email] :as user} (datomic.user/by-email email datomic)
        password-reset (adapters.user/internal->password-reset-datomic id)]
    (when user
      (some-> (datomic.password-reset/insert! password-reset datomic)
              :password-reset/id
              (diplomatic.producer/send-password-reset-notification! email producer)))))
