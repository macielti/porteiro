(ns porteiro.controllers.user
  (:require [schema.core :as s]
            [buddy.hashers :as hashers]
            [porteiro.adapters.user :as adapters.user]
            [porteiro.db.datomic.user :as datomic.user]
            [porteiro.wire.datomic.user :as wire.datomic.user]
            [porteiro.models.user :as models.user]
            [porteiro.db.datomic.password-reset :as datomic.password-reset]
            [porteiro.db.datomic.contact :as database.contact]
            [porteiro.diplomatic.producer :as diplomatic.producer]))

(s/defn create-user! :- models.user/User
  [user :- wire.datomic.user/User
   email :- s/Str
   datomic
   producer]
  (datomic.user/insert! user datomic)
  (diplomatic.producer/create-email-contact! user email producer)
  user)

(s/defn update-password!
  [{:password-update/keys [old-password new-password] :as password-update} :- models.user/PasswordUpdate
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

(s/defn reset-password!
  [{:password-reset/keys [email]} :- models.user/PasswordReset
   producer
   datomic]
  (let [{:user/keys [id] :as user} (datomic.user/by-email email datomic)
        password-reset (adapters.user/internal->password-reset-datomic id)
        contact        (some-> id
                               (database.contact/by-user-id datomic)
                               first)]
    (when user
      (some-> (datomic.password-reset/insert! password-reset datomic)
              :password-reset/id
              (diplomatic.producer/send-password-reset-notification! (:contact/email contact) producer)))))
