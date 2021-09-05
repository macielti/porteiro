(ns microservice-user-management.interceptors.password-reset
  (:use [clojure pprint])
  (:require [microservice-user-management.db.datomic.password-reset :as datomic.password-reset]
            [clojure.tools.logging :as log])
  (:import (java.util UUID)))

(def invalid-password-reset-token-exception
  (ex-info "Invalid/Expired password reset token" {:status 401
                                                   :reason "Invalid/Expired password reset token"}))

(def valid-password-reset-consolidation-token
  {:name  ::valid-password-reset-consolidation-token
   :enter (fn [{{{:keys [token]}   :json-params
                 {:keys [datomic]} :components} :request :as context}]
            (try (let [reset-password (datomic.password-reset/valid-and-free-password-reset-by-token {:token (UUID/fromString token)} datomic)]
                   (if (empty? reset-password)
                     (throw invalid-password-reset-token-exception)))
                 (catch Exception e
                   (log/error e)
                   (throw invalid-password-reset-token-exception)))

            context)})
