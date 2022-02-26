(ns porteiro.controllers.user
  (:require [schema.core :as s]
            [buddy.hashers :as hashers]
            [porteiro.adapters.user :as adapters.user]
            [porteiro.db.datomic.user :as datomic.user]
            [porteiro.wire.datomic.user :as wire.datomic.user]
            [porteiro.models.user :as models.user]
            [porteiro.models.contact :as models.contact]
            [porteiro.db.datomic.password-reset :as datomic.password-reset]
            [porteiro.db.datomic.contact :as database.contact]
            [porteiro.diplomatic.producer :as diplomatic.producer]
            [porteiro.db.datomic.user :as database.user]))

(s/defn create-user! :- models.user/User
  [user :- wire.datomic.user/User
   email-contact :- models.contact/Contact
   datomic]
  (database.user/insert-use-with-contact! user email-contact datomic)
  user)

(s/defn update-password!
  [{:password-update/keys [old-password new-password]} :- models.user/PasswordUpdate
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

(s/defn add-role! :- models.user/User
  [user-id :- s/Uuid
   role :- wire.datomic.user/UserRoles
   datomic-connection]
  (if (database.user/by-id user-id datomic-connection)
    (do (database.user/add-role! user-id role datomic-connection)
        (database.user/by-id user-id datomic-connection))
    (throw (ex-info "User not found"
                    {:status 404
                     :cause  "User not found"}))))
