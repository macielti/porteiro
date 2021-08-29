(ns microservice-user-management.adapters.user
  (:require [schema.core :as s]
            [humanize.schema :as h]
            [microservice-user-management.wire.in.user :as wire.in.user]
            [microservice-user-management.wire.datomic.user :as wire.datomic.user]
            [microservice-user-management.wire.out.user :as wire.out.user]
            [microservice-user-management.models.user :as models.user]
            [buddy.hashers :as hashers]
            [microservice-user-management.wire.datomic.password-reset :as wire.datomic.password-reset])
  (:import (java.util UUID Date)
           (clojure.lang ExceptionInfo)))

(s/defn wire->password-update-internal :- models.user/PasswordUpdate
  [{:keys [oldPassword newPassword] :as password-update} :- wire.in.user/PasswordUpdate]
  (try
    (s/validate wire.in.user/PasswordUpdate password-update)
    {:old-password oldPassword
     :new-password newPassword}
    (catch ExceptionInfo e
      (if (= (-> e ex-data :type)
             :schema.core/error)
        (throw (ex-info "Schema error"
                        {:status 422
                         :cause  (get-in (h/ex->err e) [:unknown :error])}))))))

(s/defn wire->password-reset-internal :- wire.in.user/PasswordReset
  [password-reset :- wire.in.user/PasswordReset]
  (try
    (s/validate wire.in.user/PasswordReset password-reset)
    (catch ExceptionInfo e
      (if (= (-> e ex-data :type)
             :schema.core/error)
        (throw (ex-info "Schema error"
                        {:status 422
                         :cause  (get-in (h/ex->err e) [:unknown :error])}))))))

(s/defn internal->password-reset-datomic :- wire.datomic.password-reset/PasswordReset
  [user-id :- s/Uuid]
  {:password-reset/id         (UUID/randomUUID)
   :password-reset/user-id    user-id
   :password-reset/created-at (Date.)})

(s/defn wire->create-user-internal :- wire.in.user/User
  [user :- wire.in.user/User]
  (try
    (s/validate wire.in.user/User user)
    (catch ExceptionInfo e
      (if (= (-> e ex-data :type)
             :schema.core/error)
        (throw (ex-info "Schema error"
                        {:status 422
                         :cause  (get-in (h/ex->err e) [:unknown :error])}))))))

(s/defn internal->create-user-datomic :- wire.datomic.user/User
  [{:keys [username password email]} :- wire.in.user/User]
  #:user {:id              (UUID/randomUUID)
          :username        username
          :email           email
          :hashed-password (hashers/derive password)})

(s/defn datomic->wire :- wire.out.user/User
  [{:user/keys [id username email]} :- wire.datomic.user/User]
  {:id       (str id)
   :username username
   :email    email})
