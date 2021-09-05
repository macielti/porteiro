(ns microservice-user-management.adapters.password
  (:require [schema.core :as s]
            [microservice-user-management.wire.in.password :as wire.in.password]
            [microservice-user-management.models.password-reset :as models.password]
            [humanize.schema :as h]
            [buddy.hashers :as hashers])
  (:import (clojure.lang ExceptionInfo)
           (java.util UUID)))

(s/defn wire->password-reset-consolidation-internal :- models.password/PasswordResetConsolidation
  [{:keys [token newPassword] :as password-reset-consolidation} :- wire.in.password/PasswordResetConsolidation]
  (try
    (s/validate wire.in.password/PasswordResetConsolidation password-reset-consolidation)
    {:token           (UUID/fromString token)
     :hashed-password (hashers/derive newPassword)}
    (catch ExceptionInfo e
      (if (= (-> e ex-data :type)
             :schema.core/error)
        (throw (ex-info "Schema error"
                        {:status 422
                         :cause  (get-in (h/ex->err e) [:unknown :error])}))))))
