(ns porteiro.interceptors.user
  (:require [datalevin.core :as d]
            [porteiro.db.datomic.user :as datomic.user]
            [porteiro.db.datalevin.user :as datalevin.user]
            [common-clj.error.core :as common-error]))

(def username-already-in-use-interceptor-datomic
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

(def username-already-in-use-interceptor-datalevin
  {:name  ::username-already-in-use-interceptor
   :enter (fn [{{json-params         :json-params
                 {:keys [datalevin]} :components} :request :as context}]
            (let [username (or (-> json-params :user :username) "")
                  user (datalevin.user/by-username username (d/db datalevin))]
              (when-not (empty? user)
                (common-error/http-friendly-exception 409
                                                      "not-unique"
                                                      "Username already in use"
                                                      "username already in use by other user")))
            context)})

(def email-already-in-use-interceptor-datomic
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

(def email-already-in-use-interceptor-datalevin
  {:name  ::email-already-in-use-interceptor
   :enter (fn [{{json-params         :json-params
                 {:keys [datalevin]} :components} :request :as context}]
            (let [email (or (-> json-params :contact :email) "")
                  user (datalevin.user/by-email email (d/db datalevin))]
              (when-not (empty? user)
                (common-error/http-friendly-exception 409
                                                      "not-unique"
                                                      "Email already in use"
                                                      "Email already in use by other user")))
            context)})