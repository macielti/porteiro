(ns porteiro.interceptors.password-reset
  (:require [porteiro.db.datomic.password-reset :as datomic.password-reset]
            [porteiro.adapters.password-reset :as adapters.password-reset]))

(def invalid-password-reset-token-exception
  (ex-info "Invalid/Expired password reset token" {:status 401
                                                   :reason "Invalid/Expired password reset token"}))

(def valid-password-reset-consolidation-token
  {:name  ::valid-password-reset-consolidation-token
   :enter (fn [{{password-reset :json-params
                 {:keys [datomic]}                  :components} :request :as context}]
            (let [reset-password (datomic.password-reset/valid-and-free-password-reset-by-token
                                   (adapters.password-reset/wire->password-reset-consolidation-internal password-reset)
                                   datomic)]
              (if (empty? reset-password)
                (throw invalid-password-reset-token-exception)))
            context)})
