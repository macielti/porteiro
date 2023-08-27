(ns porteiro.interceptors.customer
  (:require [porteiro.db.postgres.customer :as database.customer]
            [common-clj.error.core :as common-error]))

(def username-already-in-use-interceptor
  {:name  ::username-already-in-use-interceptor
   :enter (fn [{{json-params          :json-params
                 {:keys [postgresql]} :components} :request :as context}]
            (let [username (or (-> json-params :customer :username) "")
                  customer (database.customer/by-username username postgresql)]
              (when-not (empty? customer)
                (common-error/http-friendly-exception 409
                                                      "not-unique"
                                                      "Username already in use"
                                                      "username already in use by other customer")))
            context)})

