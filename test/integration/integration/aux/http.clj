(ns integration.aux.http
  (:require [clojure.test :refer :all]
            [io.pedestal.test :as test]
            [cheshire.core :as json]))

(defn create-user!
  [user
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :post "/users"
                                                 :headers {"Content-Type" "application/json"}
                                                 :body (json/encode user))]
    {:status status
     :body   (json/decode body true)}))

(defn fetch-contacts
  [token
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :get "/users/contacts"
                                                 :headers {"Content-Type"  "application/json"
                                                           "Authorization" (str "Bearer " token)})]
    {:status status
     :body   (json/decode body true)}))

(defn authenticate-user!
  [auth
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :post "/users/auth"
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
                                                 :put "/users/password"
                                                 :headers {"Content-Type"  "application/json"
                                                           "Authorization" (str "Bearer " token)}
                                                 :body (json/encode password-update))]
    {:status status
     :body   (json/decode body true)}))

(defn request-reset-password!
  [password-reset service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :post "/users/request-password-reset"
                                                 :headers {"Content-Type" "application/json"}
                                                 :body (json/encode password-reset))]
    {:status status
     :body   (json/decode body true)}))

(defn reset-password!
  [password-reset service-fn]
  (let [{:keys [status]} (test/response-for service-fn
                                            :post "/users/password-reset"
                                            :headers {"Content-Type" "application/json"}
                                            :body (json/encode password-reset))]
    {:status status
     :body   nil}))

(defn add-role!
  [token user-id role service-fn]
  (let [{:keys [status body]} (test/response-for service-fn
                                                 :post (format "/users/roles?role=%s&user-id=%s" role user-id)
                                                 :headers {"Content-Type"  "application/json"
                                                           "Authorization" (str "Bearer " token)})]
    {:status status
     :body   (json/decode body true)}))
