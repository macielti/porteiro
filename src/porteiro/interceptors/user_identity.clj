(ns porteiro.interceptors.user-identity
  (:require [schema.core :as s]
            [buddy.sign.jwt :as jwt]
            [clojure.string :as str]
            [porteiro.wire.datomic.user :as wire.datomic.user]
            [porteiro.db.datomic.user :as database.user])
  (:import (java.util UUID)
           (clojure.lang ExceptionInfo)))


(s/defschema UserIdentity
  {:user-identity/id s/Uuid})

(s/defn ^:private wire-jwt->user-identity :- UserIdentity
  [jwt-wire :- s/Str
   jwt-secret :- s/Str]
  (try (let [{:keys [id]} (:user (jwt/unsign jwt-wire jwt-secret))]
         {:user-identity/id (UUID/fromString id)})
       (catch ExceptionInfo _ (throw (ex-info "Invalid JWT"
                                              {:status 422
                                               :cause  "Invalid JWT"})))))

(def user-identity-interceptor
  {:name  ::user-identity-interceptor
   :enter (fn [{{{:keys [config]} :components
                 headers          :headers} :request :as context}]
            (assoc-in context [:request :user-identity]
                      (try (let [jw-token (-> (get headers "authorization") (str/split #" ") last)]
                             (wire-jwt->user-identity jw-token (:jwt-secret config)))
                           (catch Exception _ (throw (ex-info "Invalid JWT"
                                                              {:status 422
                                                               :cause  "Invalid JWT"}))))))})

(s/defn user-required-roles-interceptor
  [required-roles :- [wire.datomic.user/UserRoles]]
  {:name  ::user-required-roles-interceptor
   :enter (fn [{{{:keys [datomic]}           :components
                 {user-id :user-identity/id} :user-identity} :request :as context}]
            (let [{:user/keys [roles]} (database.user/by-id user-id (:connection datomic))]
              (if (empty? (clojure.set/difference (set required-roles) (set roles)))
                context
                (throw (ex-info "Insufficient privileges/roles/permission"
                                {:status 403
                                 :cause  "Insufficient privileges/roles/permission"})))))})
