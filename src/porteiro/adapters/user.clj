(ns porteiro.adapters.user
  (:require [schema.core :as s]
            [buddy.hashers :as hashers]
            [porteiro.wire.in.user :as wire.in.user]
            [porteiro.wire.out.user :as wire.out.user]
            [porteiro.models.user :as models.user]
            [porteiro.wire.datomic.password-reset :as wire.datomic.password-reset]
            [porteiro.wire.datomic.user :as wire.datomic.user]
            [camel-snake-kebab.core :as camel-snake-kebab])
  (:import (java.util UUID Date)))

(s/defn wire->password-update-internal :- models.user/PasswordUpdate
  [{:keys [oldPassword newPassword]} :- wire.in.user/PasswordUpdate]
  {:password-update/old-password oldPassword
   :password-update/new-password newPassword})

(s/defn wire->password-reset-internal :- models.user/PasswordReset
  [{:keys [email]} :- wire.in.user/PasswordReset]
  {:password-reset/email email})

(s/defn internal->password-reset-datomic :- wire.datomic.password-reset/PasswordReset
  [user-id :- s/Uuid]
  {:password-reset/id         (UUID/randomUUID)
   :password-reset/user-id    user-id
   :password-reset/state      :free
   :password-reset/created-at (Date.)})

(s/defn wire->internal-user :- models.user/User
  [{:keys [username password email] :as user} :- wire.in.user/User]
  #:user{:id              (UUID/randomUUID)
         :username        username
         :email           email
         :hashed-password (hashers/derive password)})

(s/defn internal-user->wire-datomic-user :- wire.datomic.user/User
  [user :- models.user/User]
  (dissoc user :user/email))

(s/defn internal-user->wire :- wire.out.user/UserDocument
  [{:user/keys [id username roles] :or {roles []}} :- models.user/UserWithoutEmail
   email :- s/Str]
  {:user {:id       (str id)
          :username username
          :roles    (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING roles)
          :email    email}})

(s/defn internal-user-authentication->wire :- wire.out.user/UserDocument
  [{:user/keys [id username roles] :or {roles []}} :- models.user/UserWithoutEmail

   email :- s/Str]
  {:user {:id       (str id)
          :username username
          :roles    (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING roles)
          :email    email}})

(s/defn internal-user->wire-without-email :- wire.out.user/UserDocument
  [{:user/keys [id username roles] :or {roles []}} :- models.user/UserWithoutEmail]
  {:user {:id       (str id)
          :username username
          :roles    (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING roles)}})

(s/defn wire->internal-role :- wire.datomic.user/UserRoles
  [wire-role :- wire.in.user/UserRoles]
  (camel-snake-kebab/->kebab-case-keyword wire-role))
