(ns integration.aux.http
  (:use [clojure pprint])
  (:require [clojure.test :refer :all]
            [io.pedestal.test :as test]
            [cheshire.core :as json]))

(defn create-user
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

(defn healthy-check
  [service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :get "/healthy")]
    {:status status
     :body   (json/decode body true)}))
