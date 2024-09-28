(ns porteiro.controllers.customer
  (:require [buddy.hashers :as hashers]
            [common-clj.error.core :as common-error]
            [datomic.api :as d]
            [porteiro.adapters.customer :as adapters.user]
            [porteiro.db.datomic.contact :as database.contact]
            [porteiro.db.datomic.customer :as database.customer]
            [porteiro.db.datomic.password-reset :as database.password-reset]
            [porteiro.diplomat.producer :as diplomat.producer]
            [porteiro.models.contact :as models.contact]
            [porteiro.models.customer :as models.customer]
            [schema.core :as s]))

(s/defn create-customer! :- models.customer/Customer
  [customer :- models.customer/Customer
   email-contact :- models.contact/Contact
   datomic]
  (database.customer/insert-customer-with-contact! customer email-contact datomic)
  customer)

(s/defn update-password!
  [{:password-update/keys [old-password new-password]} :- models.customer/PasswordUpdate
   customer-id :- s/Uuid
   datomic]
  (let [{:customer/keys [hashed-password] :as customer} (database.customer/lookup customer-id (d/db datomic))]
    (if (and customer
             (:valid (hashers/verify old-password hashed-password)))
      (-> (assoc customer :customer/hashed-password (hashers/derive new-password))
          (database.customer/insert! datomic))
      (common-error/http-friendly-exception 403
                                            "invalid-credentials"
                                            "The old password you have entered is incorrect"
                                            "Incorrect old password"))))

(s/defn reset-password!
  [{:password-reset/keys [email]} :- models.customer/PasswordReset
   producer
   datomic]
  (let [{:customer/keys [id] :as user} (database.customer/by-email email (d/db datomic))
        password-reset (adapters.user/internal->password-reset-datomic id)
        contact (some-> id
                        (database.contact/by-customer-id (d/db datomic))
                        first)]
    (when user
      (some-> (database.password-reset/insert! password-reset datomic)
              :password-reset/id
              (diplomat.producer/send-password-reset-notification! (:contact/email contact) producer)))))

(s/defn add-role! :- models.customer/Customer
  [customer-id :- s/Uuid
   role :- s/Keyword
   datomic]
  (if (database.customer/lookup customer-id (d/db datomic))
    (do (database.customer/add-role! customer-id role datomic)
        (database.customer/lookup customer-id (d/db datomic)))
    (throw (ex-info "Customer not found"
                    {:status 404
                     :cause  "Customer not found"}))))
