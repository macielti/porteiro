(ns porteiro.wire.in.user
  (:require [schema.core :as s]
            [porteiro.wire.out.user :as wire.out.user]
            [porteiro.wire.in.contact :as wire.in.contact]))

(def UserRoles wire.out.user/UserRoles)

(s/defschema User
  "Schema for user creation request"
  {:username s/Str
   :password s/Str})

(s/defschema UserCreationDocument
  "Schema for user creation request"
  {:user    User
   :contact wire.in.contact/ContactWithoutUserId})

(s/defschema PasswordUpdate
  "Schema for password update request"
  {:oldPassword s/Str
   :newPassword s/Str})

(s/defschema PasswordReset
  "Schema for password reset request"
  {:email s/Str})
