(ns porteiro.interceptors.customer
  (:require [common-clj.error.core :as common-error]
            [datomic.api :as d]
            [porteiro.db.datomic.customer :as database.customer]))

(def username-already-in-use-interceptor
  {:name  ::username-already-in-use-interceptor
   :enter (fn [{{json-params       :json-params
                 {:keys [datomic]} :components} :request :as context}]
            (let [username (get-in json-params [:customer :username] "")
                  customer (database.customer/by-username username (d/db datomic))]
              (when-not (empty? customer)
                (common-error/http-friendly-exception 409
                                                      "not-unique"
                                                      "Username already in use"
                                                      "username already in use by other customer")))
            context)})

