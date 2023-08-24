(ns porteiro.adapters.customer
  (:require [medley.core :as medley]
            [porteiro.models.customer :as models.customer]
            [schema.core :as s]
            [buddy.hashers :as hashers]
            [porteiro.wire.in.user :as wire.in.user]
            [porteiro.wire.out.user :as wire.out.user]
            [porteiro.models.customer :as models.customer]
            [porteiro.wire.datomic.password-reset :as wire.datomic.password-reset]
            [porteiro.wire.datomic.user :as wire.datomic.user]
            [camel-snake-kebab.core :as camel-snake-kebab]
            [porteiro.wire.postgresql.customer :as wire.postgresql.customer])
  (:import (java.util UUID Date)))

(s/defn wire->password-update-internal :- models.customer/PasswordUpdate
  [{:keys [oldPassword newPassword]} :- wire.in.user/PasswordUpdate]
  {:password-update/old-password oldPassword
   :password-update/new-password newPassword})

(s/defn wire->password-reset-internal :- models.customer/PasswordReset
  [{:keys [email]} :- wire.in.user/PasswordReset]
  {:password-reset/email email})

(s/defn internal->password-reset-datomic :- wire.datomic.password-reset/PasswordReset
  [user-id :- s/Uuid]
  {:password-reset/id         (UUID/randomUUID)
   :password-reset/user-id    user-id
   :password-reset/state      :free
   :password-reset/created-at (Date.)})

(s/defn wire->internal-user :- models.customer/Customer
  [{:keys [username password]} :- wire.in.user/User]
  {:user/id              (UUID/randomUUID)
   :user/username        username
   :user/hashed-password (hashers/derive password)})

(s/defn internal-user->wire-datomic-user :- wire.datomic.user/User
  [user :- models.customer/Customer]
  (dissoc user :user/email))

(s/defn internal-user->wire :- wire.out.user/User
  [{:user/keys [id username roles] :or {roles []}} :- models.customer/UserWithoutEmail]
  {:id       (str id)
   :username username
   :roles    (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING roles)})

(s/defn internal-user->wire-without-email :- wire.out.user/UserDocument
  [{:user/keys [id username roles] :or {roles []}} :- models.customer/UserWithoutEmail]
  {:user {:id       (str id)
          :username username
          :roles    (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING roles)}})

(s/defn wire->internal-role :- wire.datomic.user/UserRoles
  [wire-role :- wire.in.user/UserRoles]
  (camel-snake-kebab/->kebab-case-keyword wire-role))

(s/defn postgresql->internal :- models.customer/Customer
  [{:keys [id username hashed_password roles]} :- wire.postgresql.customer/Customer]
  (medley/assoc-some {:customer/id              id
                      :customer/username        username
                      :customer/hashed-password hashed_password}
                     :customer/roles (when roles
                                       (map camel-snake-kebab/->snake_case_keyword
                                            (.getArray roles)))))