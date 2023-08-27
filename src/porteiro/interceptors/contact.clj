(ns porteiro.interceptors.contact
  (:require [common-clj.error.core :as common-error]
            [porteiro.db.postgres.contact :as database.contact]))

(def email-already-in-use-interceptor
  {:name  ::email-already-in-use-interceptor
   :enter (fn [{{json-params        :json-params
                 {:keys [postgresql]} :components} :request :as context}]
            (let [email (or (-> json-params :contact :email) "")
                  contact (database.contact/by-email email postgresql)]
              (when-not (empty? contact)
                (common-error/http-friendly-exception 409
                                                      "not-unique"
                                                      "Email already in use"
                                                      "Email already in use as contact by another user")))
            context)})