(ns porteiro.interceptors.contact
  (:require [common-clj.error.core :as common-error]
            [datomic.api :as d]
            [porteiro.db.datomic.contact :as database.contact]))

(def email-already-in-use-interceptor
  {:name  ::email-already-in-use-interceptor
   :enter (fn [{{json-params       :json-params
                 {:keys [datomic]} :components} :request :as context}]
            (let [email (get-in json-params [:contact :email] "")
                  contact (database.contact/by-email email (d/db datomic))]
              (when-not (empty? contact)
                (common-error/http-friendly-exception 409
                                                      "not-unique"
                                                      "Email already in use"
                                                      "Email already in use as contact by another user")))
            context)})
