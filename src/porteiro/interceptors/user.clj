(ns porteiro.interceptors.user
  (:require [porteiro.db.datomic.user :as datomic.user]))

(def username-already-in-use-interceptor
  {:name  ::user-already-in-use-interceptor
   :enter (fn [{{{:keys [username] :or {username ""}} :json-params
                 {:keys [datomic]}                    :components} :request :as context}]
            (let [user (datomic.user/by-username username (:connection datomic))]
              (if-not (empty? user)
                (throw (ex-info "Username already in use" {:status 409
                                                           :cause  "username already in use by other user"}))))
            context)})
