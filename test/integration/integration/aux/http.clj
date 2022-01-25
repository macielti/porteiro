(ns integration.aux.http
  (:require [clojure.test :refer :all]
            [io.pedestal.test :as test]
            [cheshire.core :as json]))

;TODO: So much repeated code 🤮. This namespace code stinks!

(defn create-user!
  [user
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :post "/users"
                                                 :headers {"Content-Type" "application/json"}
                                                 :body (json/encode user))]
    {:status status
     :body   (json/decode body true)}))

(defn auth
  [auth
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :post "/auth"
                                                 :headers {"Content-Type" "application/json"}
                                                 :body (json/encode auth))]
    {:status status
     :body   (json/decode body true)}))

(defn health-check
  [service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :get "/health")]
    {:status status
     :body   (json/decode body true)}))

(defn update-password!
  [password-update token service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :put "/user/password"
                                                 :headers {"Content-Type"  "application/json"
                                                           "Authorization" (str "Bearer " token)}
                                                 :body (json/encode password-update))]
    {:status status
     :body   (json/decode body true)}))

(defn reset-password!
  [password-reset service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :post "/user/password-reset"
                                                 :headers {"Content-Type" "application/json"}
                                                 :body (json/encode password-reset))]
    {:status status
     :body   (json/decode body true)}))

(defn consolidate-reset-password!
  [password-reset service-fn]
  (let [{:keys [status]} (test/response-for service-fn
                                            :put "/user/password-reset"
                                            :headers {"Content-Type" "application/json"}
                                            :body (json/encode password-reset))]
    {:status status
     :body   nil}))
