(ns porteiro.adapters.user
  (:require [schema.core :as s]
            [humanize.schema :as h]
            [buddy.hashers :as hashers]
            [porteiro.wire.in.user :as wire.in.user]
            [porteiro.wire.out.user :as wire.out.user]
            [porteiro.models.user :as models.user]
            [porteiro.wire.datomic.password-reset :as wire.datomic.password-reset]
            [porteiro.wire.datomic.user :as wire.datomic.user])
  (:import (java.util UUID Date)
           (clojure.lang ExceptionInfo)))

(s/defn wire->password-update-internal :- models.user/PasswordUpdate
  [{:keys [oldPassword newPassword] :as password-update} :- wire.in.user/PasswordUpdate]
  (try
    (s/validate wire.in.user/PasswordUpdate password-update)
    #:password-update{:old-password oldPassword
                      :new-password newPassword}
    (catch ExceptionInfo e
      (if (= (-> e ex-data :type)
             :schema.core/error)
        (throw (ex-info "Schema error"
                        {:status 422
                         :cause  (get-in (h/ex->err e) [:unknown :error])}))))))

(s/defn wire->password-reset-internal :- models.user/PasswordReset
  [{:keys [email] :as password-reset} :- wire.in.user/PasswordReset]
  (try
    (s/validate wire.in.user/PasswordReset password-reset)
    #:password-reset {:email email}
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
   :password-reset/state      :free
   :password-reset/created-at (Date.)})

(s/defn wire->internal-user :- models.user/User
  [{:keys [username password email] :as user} :- wire.in.user/User]
  (try
    (s/validate wire.in.user/User user)
    #:user{:id              (UUID/randomUUID)
           :username        username
           :email           email
           :hashed-password (hashers/derive password)}
    (catch ExceptionInfo e
      (if (= (-> e ex-data :type)
             :schema.core/error)
        (throw (ex-info "Schema error"
                        {:status 422
                         :cause  (get-in (h/ex->err e) [:unknown :error])}))))))

(s/defn internal-user->wire-datomic-user :- wire.datomic.user/User
  [user :- models.user/User]
  (dissoc user :user/email))

(s/defn internal-user->wire :- wire.out.user/User
  [{:user/keys [id username email]} :- models.user/User]
  {:id       (str id)
   :username username
   :email    email})
