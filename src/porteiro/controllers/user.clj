(ns porteiro.controllers.user
  (:require [datalevin.core :as d]
            [schema.core :as s]
            [buddy.hashers :as hashers]
            [porteiro.adapters.customer :as adapters.user]
            [porteiro.db.datalevin.user :as database.user]
            [porteiro.wire.datomic.user :as wire.datomic.user]
            [porteiro.models.customer :as models.user]
            [porteiro.models.contact :as models.contact]
            [porteiro.db.datalevin.password-reset :as database.password-reset]
            [porteiro.db.datalevin.contact :as database.contact]
            [porteiro.diplomat.producer :as diplomat.producer]
            [porteiro.db.datalevin.user :as database.user]
            [common-clj.error.core :as common-error]))

(s/defn create-user! :- models.user/Customer
  [user :- wire.datomic.user/User
   email-contact :- models.contact/Contact
   datalevin-connection]
  (database.user/insert-user-with-contact! user email-contact datalevin-connection)
  user)

(s/defn update-password!
  [{:password-update/keys [old-password new-password]} :- models.user/PasswordUpdate
   user-id :- s/Uuid
   datalevin-connection]
  (let [{:user/keys [hashed-password] :as user-datomic} (database.user/lookup user-id (d/db datalevin-connection))]
    (if (and user-datomic
             (:valid (hashers/verify old-password hashed-password)))
      (-> (assoc user-datomic :user/hashed-password (hashers/derive new-password))
          (database.user/insert! datalevin-connection))
      (common-error/http-friendly-exception 403
                                            "invalid-credentials"
                                            "The old password you have entered is incorrect"
                                            "Incorrect old password"))))

(s/defn reset-password!
  [{:password-reset/keys [email]} :- models.user/PasswordReset
   producer
   datalevin-connection]
  (let [{:user/keys [id] :as user} (database.user/by-email email (d/db datalevin-connection))
        password-reset (adapters.user/internal->password-reset-datomic id)
        contact (some-> id
                        (database.contact/by-user-id (d/db datalevin-connection))
                        first)]
    (when user
      (some-> (database.password-reset/insert! password-reset datalevin-connection)
              :password-reset/id
              (diplomat.producer/send-password-reset-notification! (:contact/email contact) producer)))))

(s/defn add-role! :- models.user/Customer
  [user-id :- s/Uuid
   role :- wire.datomic.user/UserRoles
   datalevin-connection]
  (if (database.user/lookup user-id (d/db datalevin-connection))
    (do (database.user/add-role! user-id role datalevin-connection)
        (database.user/lookup user-id (d/db datalevin-connection)))
    (throw (ex-info "User not found"
                    {:status 404
                     :cause  "User not found"}))))
