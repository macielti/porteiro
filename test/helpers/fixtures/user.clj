(ns fixtures.user
  (:require [schema-generators.complete :as c]
            [porteiro.wire.datomic.user :as wire.datomic.user])
  (:import (java.util UUID)))

(def user-id (UUID/randomUUID))
(def wire-user-id (str (UUID/randomUUID)))

(def user {:username "ednaldo-pereira"
           :email    "example@example.com"
           :password "some-strong-password"})

(def user-auth (dissoc user :email))

(def password-update {:oldPassword (:password user)
                      :newPassword "new-strong-password"})

(def datomic-user (c/complete {:user/id              user-id
                               :user/username        "ednaldo-pereira"
                               :user/hashed-password "password-hash"} wire.datomic.user/User))
