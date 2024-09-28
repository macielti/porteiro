(ns porteiro.interceptors.password-reset
  (:require [common-clj.error.core :as common-error]
            [datomic.api :as d]
            [porteiro.adapters.password-reset :as adapters.password-reset]
            [porteiro.db.datomic.password-reset :as database.password-reset]))

(def valid-password-reset-execution-token
  {:name  ::valid-password-reset-execution-token
   :enter (fn [{{password-reset    :json-params
                 {:keys [datomic]} :components} :request :as context}]
            (let [reset-password (database.password-reset/valid-password-reset-by-token
                                  (-> (adapters.password-reset/wire->password-reset-execution-internal password-reset)
                                      :password-reset-execution/token)
                                  (d/db datomic))]
              (when (empty? reset-password)
                (common-error/http-friendly-exception 401
                                                      "invalid-expired-token"
                                                      "Invalid/Expired password reset token"
                                                      "Invalid/Expired password reset token")))
            context)})
