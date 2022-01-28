(ns porteiro.diplomatic.http-server
  (:require [porteiro.diplomatic.http-server.healthy :as diplomatic.http-server.healthy]
            [porteiro.interceptors.user :as interceptors.user]
            [porteiro.diplomatic.http-server.user :as diplomatic.http-server.user]
            [porteiro.diplomatic.http-server.auth :as diplomatic.http-server.auth]))


(def routes [["/health" :get diplomatic.http-server.healthy/healthy-check :route-name :health-check]
             ["/users" :post [interceptors.user/username-already-in-use-interceptor
                              diplomatic.http-server.user/create-user!] :route-name :create-user]
             ["/users/auth" :post diplomatic.http-server.auth/authenticate-user! :route-name :user-authentication]])
