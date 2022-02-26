(ns porteiro.adapters.auth
  (:require [schema.core :as s]
            [humanize.schema :as h]
            [porteiro.wire.in.auth :as wire.in.auth]
            [porteiro.models.auth :as models.auth])
  (:import (clojure.lang ExceptionInfo)))

(s/defn wire->internal-user-auth :- models.auth/UserAuth
  [{:keys [username password] :as auth} :- wire.in.auth/UserAuth]
  (try
    (s/validate wire.in.auth/UserAuth auth)
    #:user-auth{:username username
                :password password}
    (catch ExceptionInfo e
      (if (= (-> e ex-data :type)
             :schema.core/error)
        (throw (ex-info "Schema error"
                        {:status 422
                         :cause  (get-in (h/ex->err e) [:unknown :error])}))))))
