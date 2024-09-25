(ns porteiro.models.customer
  (:require [schema.core :as s]))

(def roles #{:admin :test})
(def CustomerRoles (apply s/enum roles))

(def base-customer
  {:customer/id                     s/Uuid
   :customer/username               s/Str
   (s/optional-key :customer/roles) [CustomerRoles]
   :customer/hashed-password        s/Str})

(s/defschema Customer
  base-customer)

(s/defschema CustomerWithoutEmail
  (dissoc base-customer :customer/email))

(s/defschema PasswordUpdate
  "Schema for password update"
  #:password-update {:old-password s/Str
                     :new-password s/Str})

(s/defschema PasswordReset
  #:password-reset {:email s/Str})
