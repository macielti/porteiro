(ns porteiro.wire.in.customer
  (:require [schema.core :as s]
            [porteiro.wire.out.customer :as wire.out.user]
            [porteiro.wire.in.contact :as wire.in.contact]))

(def UserRoles wire.out.user/CustomerRoles)

(s/defschema Customer
  "Schema for customer creation request"
  {:username s/Str
   :password s/Str})

(s/defschema CustomerCreationDocument
  "Schema for customer creation request"
  {:customer Customer
   :contact  wire.in.contact/ContactWithoutCustomerId})

(s/defschema PasswordUpdate
  "Schema for password update request"
  {:oldPassword s/Str
   :newPassword s/Str})

(s/defschema PasswordReset
  "Schema for password reset request"
  {:email s/Str})
