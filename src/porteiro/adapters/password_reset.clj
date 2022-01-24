(ns porteiro.adapters.password-reset
  (:require [schema.core :as s]
            [porteiro.wire.in.password-reset :as wire.in.password-reset]
            [porteiro.models.password-reset :as models.password]
            [humanize.schema :as h]
            [buddy.hashers :as hashers])
  (:import (java.util UUID)
           (clojure.lang ExceptionInfo)))

(s/defn wire->password-reset-consolidation-internal :- models.password/PasswordResetConsolidation
  [{:keys [token newPassword] :as password-reset-consolidation} :- wire.in.password-reset/PasswordResetConsolidation]
  (try
    (s/validate wire.in.password-reset/PasswordResetConsolidation password-reset-consolidation)
    {:token           (UUID/fromString token)
     :hashed-password (hashers/derive newPassword)}
    (catch ExceptionInfo e
      (if (= (-> e ex-data :type)
             :schema.core/error)
        (throw (ex-info "Schema error"
                        {:status 422
                         :cause  (get-in (h/ex->err e) [:unknown :error])}))))))
