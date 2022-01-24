(ns porteiro.adapters.auth
  (:use [clojure pprint])
  (:require [schema.core :as s]
            [porteiro.wire.in.auth :as wire.in.auth]
            [humanize.schema :as h]
            [clojure.string :as str]
            [buddy.sign.jwt :as jwt]
            [cheshire.core :as json]
            [clojure.tools.logging :as log])
  (:import (clojure.lang ExceptionInfo)
           (java.util UUID Base64)))

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
  ([jw-token jwt-secret]
   (let [{:keys [id] :as user} (try
                                 (jwt/unsign jw-token jwt-secret)
                                 (catch ExceptionInfo _ (throw (ex-info "Invalid token"
                                                                        {:status 422
                                                                         :cause  "Invalid token"}))))]
     (assoc user :id (UUID/fromString id)))))

(defn decoded-jwt
  [jwt-token]
  (try
    (let [[_ payload _] (str/split jwt-token #"\." 3)
          clj-payload (-> (.decode (Base64/getDecoder) ^String payload)
                          (String.)
                          (json/decode true))]
      (assoc clj-payload :id (UUID/fromString (:id clj-payload))))
    (catch Exception e (do
                         (log/warn :invalid-token :exception e {:jwt-token jwt-token})
                         (throw (ex-info "Invalid token"
                                         {:status 422
                                          :cause  "Invalid token"}))))))
