(ns porteiro.models.customer
  (:require [schema.core :as s]
            [porteiro.wire.datomic.user :as wire.datomic.user]))

(def base-customer
  {:customer/id                     s/Uuid
   :customer/username               s/Str
   (s/optional-key :customer/roles) [wire.datomic.user/UserRoles]
   (s/optional-key :customer/email) s/Str
   :customer/hashed-password        s/Str})

(s/defschema Customer
  base-customer)

(s/defschema UserWithoutEmail
  (dissoc base-customer :user/email))

(s/defschema PasswordUpdate
  "Schema for password update"
  #:password-update {:old-password s/Str
                     :new-password s/Str})

(s/defschema PasswordReset
  #:password-reset {:email s/Str})
