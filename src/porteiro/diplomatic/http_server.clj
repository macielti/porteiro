(ns porteiro.diplomatic.http-server
  (:require [porteiro.diplomatic.http-server.healthy :as diplomatic.http-server.healthy]
            [porteiro.interceptors.user :as interceptors.user]
            [porteiro.diplomatic.http-server.user :as diplomatic.http-server.user]))


(def routes [["/health" :get diplomatic.http-server.healthy/healthy-check :route-name :health-check]
             ["/users" :post [interceptors.user/username-already-in-use-interceptor
                              diplomatic.http-server.user/create-user!] :route-name :create-user]])
