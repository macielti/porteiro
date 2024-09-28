(ns porteiro.interceptors.customer-identity
  (:require [buddy.sign.jwt :as jwt]
            [camel-snake-kebab.core :as camel-snake-kebab]
            [clojure.string :as str]
            [common-clj.error.core :as common-error]
            [schema.core :as s])
  (:import (clojure.lang ExceptionInfo)
           (java.util UUID)))

(s/defschema CustomerIdentity
  {:customer-identity/id    s/Uuid
   :customer-identity/roles [s/Keyword]})

(s/defn ^:private wire-jwt->customer-identity :- CustomerIdentity
  [jwt-wire :- s/Str
   jwt-secret :- s/Str]
  (try (let [{:keys [id roles]} (:customer (jwt/unsign jwt-wire jwt-secret))]
         {:customer-identity/id    (UUID/fromString id)
          :customer-identity/roles (map camel-snake-kebab/->kebab-case-keyword roles)})
       (catch ExceptionInfo _ (throw (ex-info "Invalid JWT"
                                              {:status 422
                                               :cause  "Invalid JWT"})))))

(def customer-identity-interceptor
  {:name  ::customer-identity-interceptor
   :enter (fn [{{{:keys [config]} :components
                 headers          :headers} :request :as context}]
            (assoc-in context [:request :customer-identity]
                      (try (let [jw-token (-> (get headers "authorization") (str/split #" ") last)]
                             (wire-jwt->customer-identity jw-token (:jwt-secret config)))
                           (catch Exception _ (common-error/http-friendly-exception 422
                                                                                    "invalid-jwt"
                                                                                    "Invalid JWT"
                                                                                    "Invalid JWT")))))})

(s/defn customer-required-roles-interceptor
  [required-roles :- [s/Keyword]]
  {:name  ::customer-required-roles-interceptor
   :enter (fn [{{{customer-roles :customer-identity/roles} :customer-identity} :request :as context}]
            (if (empty? (clojure.set/difference (set required-roles) (set customer-roles)))
              context
              (common-error/http-friendly-exception 403
                                                    "insufficient-roles"
                                                    "Insufficient privileges/roles/permission"
                                                    "Insufficient privileges/roles/permission")))})
