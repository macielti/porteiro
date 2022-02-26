(ns porteiro.interceptors.user-identity
  (:require [schema.core :as s]
            [buddy.sign.jwt :as jwt]
            [clojure.string :as str]
            [porteiro.wire.datomic.user :as wire.datomic.user]
            [camel-snake-kebab.core :as camel-snake-kebab])
  (:import (java.util UUID)
           (clojure.lang ExceptionInfo)))

(s/defschema UserIdentity
  {:user-identity/id    s/Uuid
   :user-identity/roles [s/Keyword]})

(s/defn ^:private wire-jwt->user-identity :- UserIdentity
  [jwt-wire :- s/Str
   jwt-secret :- s/Str]
  (try (let [{:keys [id roles]} (:user (jwt/unsign jwt-wire jwt-secret))]
         {:user-identity/id    (UUID/fromString id)
          :user-identity/roles (map camel-snake-kebab/->kebab-case-keyword roles)})
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
   :enter (fn [{{{user-roles :user-identity/roles} :user-identity} :request :as context}]
            (if (empty? (clojure.set/difference (set required-roles) (set user-roles)))
              context
              (throw (ex-info "Insufficient privileges/roles/permission"
                              {:status 403
                               :cause  "Insufficient privileges/roles/permission"}))))})
