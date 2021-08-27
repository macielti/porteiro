(ns microservice-user-management.interceptors.user
  (:use [clojure pprint])
  (:require [microservice-user-management.db.datomic.user :as datomic.user]
            [microservice-user-management.adapters.auth :as adapters.auth]))

(def username-already-in-use-interceptor
  {:name  ::user-already-in-use-interceptor
   :enter (fn [{{{:keys [username] :or {username ""}} :json-params
                 {:keys [datomic]}                    :components} :request :as context}]
            (let [user (datomic.user/by-username username datomic)]
              (if-not (empty? user)
                (throw (ex-info "Username already in use" {:status 409
                                                           :reason "username already in use by other user"}))))
            context)})

(def auth-interceptor
  {:name  ::auth-interceptor
   :enter (fn [{{{:keys [config]} :components
                 headers          :headers} :request :as context}]
            (assoc-in context [:request :user-identity]
                      (-> (get headers "authorization")
                          (adapters.auth/jwt-wire->internal (get config
                                                                 :jw-token-secret)))))})
