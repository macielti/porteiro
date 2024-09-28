(ns fixtures.user
  (:import (java.util UUID)))

(def user-id (UUID/randomUUID))
(def admin-user-id (UUID/randomUUID))
(def wire-user-id (str (UUID/randomUUID)))
(def username "ednaldo-pereira")
(def password "some-strong-password")
(def email "example@example.com")

(def user {:username username
           :email    email
           :password password})

(def wire-user-for-customer-creation {:username username
                                      :password password})
(def wire-email-contact-for-user-creation {:type  "email"
                                           :email email})
(def wire-customer-creation {:customer wire-user-for-customer-creation
                             :contact  wire-email-contact-for-user-creation})

(def wire-admin-user-for-creation
  {:username "admin-da-massa"
   :password "admin-da-massa-password"})

(def wire-admin-email-contact-for-user-creation {:type  "email"
                                                 :email "admin-da-massa@example.com"})

(def wire-admin-user-creation
  {:user    wire-admin-user-for-creation
   :contact wire-admin-email-contact-for-user-creation})

(def user-auth (dissoc user :email))

(def admin-user-auth wire-admin-user-for-creation)

(def password-update {:oldPassword (:password user)
                      :newPassword "new-strong-password"})

