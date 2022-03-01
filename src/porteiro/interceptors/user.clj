(ns porteiro.interceptors.user
  (:require [porteiro.db.datomic.user :as datomic.user]
            [common-clj.error.core :as common-error]))

(def username-already-in-use-interceptor
  {:name  ::username-already-in-use-interceptor
   :enter (fn [{{{:keys [username] :or {username ""}} :json-params
                 {:keys [datomic]}                    :components} :request :as context}]
            (let [user (datomic.user/by-username username (:connection datomic))]
              (when-not (empty? user)
                (common-error/http-friendly-exception 409
                                                      "not-unique"
                                                      "Username already in use"
                                                      "username already in use by other user")))
            context)})

(def email-already-in-use-interceptor
  {:name  ::email-already-in-use-interceptor
   :enter (fn [{{{:keys [email] :or {email ""}} :json-params
                 {:keys [datomic]}              :components} :request :as context}]
            (let [user (datomic.user/by-email email (:connection datomic))]
              (when-not (empty? user)
                (common-error/http-friendly-exception 409
                                                      "not-unique"
                                                      "Email already in use"
                                                      "Email already in use by other user")))
            context)})
