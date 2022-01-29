(ns porteiro.models.user
  (:require [schema.core :as s]))

(s/defschema User
  #:user {:id              s/Uuid
          :username        s/Str
          :email           s/Str
          :hashed-password s/Str})

(s/defschema PasswordUpdate
  "Schema for password update"
  #:password-update {:old-password s/Str
                     :new-password s/Str})

(s/defschema PasswordReset
  #:password-reset {:email s/Str})
