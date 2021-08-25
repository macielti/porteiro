(ns microservice-user-management.adapters.auth
  (:require [schema.core :as s]
            [microservice-user-management.wire.in.auth :as wire.in.auth]
            [humanize.schema :as h]
            [clojure.string :as str]
            [buddy.sign.jwt :as jwt])
  (:import (clojure.lang ExceptionInfo)
           (java.util UUID)))

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

(defn jwt-wire->internal
  [authorization jwt-secret]
  (let [{:keys [id] :as user} (-> (str/split authorization #" ")
                                  last
                                  (jwt/unsign jwt-secret))]
    (assoc user :id (UUID/fromString id))))
