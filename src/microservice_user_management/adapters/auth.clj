(ns microservice-user-management.adapters.auth
  (:require [schema.core :as s]
            [microservice-user-management.wire.in.auth :as wire.in.auth]
            [humanize.schema :as h])
  (:import (clojure.lang ExceptionInfo)))

(s/defn wire->internal :- wire.in.auth/Auth
  [auth :- wire.in.auth/Auth]
  (try
    (s/validate wire.in.auth/Auth auth)
    (catch ExceptionInfo e
      (if (= (-> e ex-data :type)
             :schema.core/error)
        (throw (ex-info "Schema error"
                        {:status 422
                         :cause  (get-in (h/ex->err e) [:unknown :error])}))))))
