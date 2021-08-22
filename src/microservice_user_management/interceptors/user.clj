(ns microservice-user-management.interceptors.user
  (:use [clojure pprint])
  (:require [microservice-user-management.db.datomic.user :as datomic.user]))

(def username-already-in-use-interceptor
  {:name  ::user-already-in-use-interceptor
   :enter (fn [{{{:keys [username] :or {username ""}} :json-params
                 {:keys [datomic]}  :components} :request :as context}]
            (let [user (datomic.user/by-username username datomic)]
              (if-not (empty? user)
                (throw (ex-info "Username already in use" {:status 409
                                                           :reason "username already in use by other user"}))))
            context)})
