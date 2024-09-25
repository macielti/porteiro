(ns porteiro.controllers.auth
  (:require [schema.core :as s]
            [buddy.hashers :as hashers]
            [common-clj.error.core :as common-error]
            [common-clj.auth.core :as common-auth]
            [porteiro.models.auth :as models.auth]
            [porteiro.adapters.customer :as adapters.customer]
            [porteiro.db.datomic.customer :as database.customer]
            [porteiro.db.datomic.contact :as database.contact]
            [porteiro.diplomat.producer :as diplomat.producer]))

(s/defn customer-authentication! :- s/Str
  [{:customer-auth/keys [username password]} :- models.auth/CustomerAuth
   {:keys [jwt-secret]}
   producer
   datomic]
  (let [{:customer/keys [hashed-password id] :as customer} (database.customer/by-username username datomic)
        {:contact/keys [email]} (first (database.contact/by-customer-id id datomic))]
    (if (and customer (:valid (hashers/verify password hashed-password)))
      (do (diplomat.producer/send-success-auth-notification! email producer)
          (-> {:customer (adapters.customer/internal-customer->wire customer)}
              (common-auth/->token jwt-secret)))
      (common-error/http-friendly-exception 403
                                            "invalid-credentials"
                                            "Wrong username or/and password"
                                            "Customer is trying to login using invalid credentials"))))
