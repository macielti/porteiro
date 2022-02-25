(ns porteiro.models.user
  (:require [schema.core :as s]
            [porteiro.wire.datomic.user :as wire.datomic.user]))

(def base-user
  {:user/id                     s/Uuid
   :user/username               s/Str
   (s/optional-key :user/roles) [wire.datomic.user/UserRoles]
   (s/optional-key :user/email) s/Str
   :user/hashed-password        s/Str})

(s/defschema User
  base-user)

(s/defschema UserWithoutEmail
  (dissoc base-user :user/email))

(s/defschema PasswordUpdate
  "Schema for password update"
  #:password-update {:old-password s/Str
                     :new-password s/Str})

(s/defschema PasswordReset
  #:password-reset {:email s/Str})
