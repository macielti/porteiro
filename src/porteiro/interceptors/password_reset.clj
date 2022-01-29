(ns porteiro.interceptors.password-reset
  (:require [porteiro.db.datomic.password-reset :as datomic.password-reset]
            [porteiro.adapters.password-reset :as adapters.password-reset]))

(def invalid-password-reset-token-exception
  (ex-info "Invalid/Expired password reset token" {:status 401
                                                   :reason "Invalid/Expired password reset token"}))

(def valid-password-reset-execution-token
  {:name  ::valid-password-reset-execution-token
   :enter (fn [{{password-reset    :json-params
                 {:keys [datomic]} :components} :request :as context}]
            (let [reset-password (datomic.password-reset/valid-password-reset-by-token
                                   (-> (adapters.password-reset/wire->password-reset-execution-internal password-reset)
                                       :password-reset-execution/token)
                                   (:connection datomic))]
              (if (empty? reset-password)
                (throw invalid-password-reset-token-exception)))
            context)})
