(ns porteiro.adapters.customer
  (:require [buddy.hashers :as hashers]
            [camel-snake-kebab.core :as camel-snake-kebab]
            [medley.core :as medley]
            [porteiro.models.customer :as models.customer]
            [porteiro.wire.datomic.password-reset :as wire.datomic.password-reset]
            [porteiro.wire.in.customer :as wire.in.customer]
            [porteiro.wire.out.customer :as wire.out.customer]
            [schema.core :as s])
  (:import (java.util Date UUID)))

(s/defn wire->password-update-internal :- models.customer/PasswordUpdate
  [{:keys [oldPassword newPassword]} :- wire.in.customer/PasswordUpdate]
  {:password-update/old-password oldPassword
   :password-update/new-password newPassword})

(s/defn wire->password-reset-internal :- models.customer/PasswordReset
  [{:keys [email]} :- wire.in.customer/PasswordReset]
  {:password-reset/email email})

(s/defn internal->password-reset-datomic :- wire.datomic.password-reset/PasswordReset
  [user-id :- s/Uuid]
  {:password-reset/id          (UUID/randomUUID)
   :password-reset/customer-id user-id
   :password-reset/state       :free
   :password-reset/created-at  (Date.)})

(s/defn wire->internal-customer :- models.customer/Customer
  [{:keys [username password name]} :- wire.in.customer/Customer]
  (medley/assoc-some {:customer/id              (UUID/randomUUID)
                      :customer/username        username
                      :customer/hashed-password (hashers/derive password)}
                     :customer/name name))

(s/defn internal-customer->wire :- wire.out.customer/Customer
  [{:customer/keys [id username roles name] :or {roles []}} :- models.customer/Customer]
  (medley/assoc-some {:id       (str id)
                      :username username
                      :roles    (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING roles)}
                     :name name))

(s/defn internal-customer->wire-without-email :- wire.out.customer/CustomerDocument
  [{:customer/keys [id username roles] :or {roles []}} :- models.customer/CustomerWithoutEmail]
  {:customer {:id       (str id)
              :username username
              :roles    (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING roles)}})

(s/defn wire->internal-role :- s/Keyword
  [wire-role :- s/Str]
  (camel-snake-kebab/->kebab-case-keyword wire-role))
