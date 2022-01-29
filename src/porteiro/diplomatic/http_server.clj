(ns porteiro.diplomatic.http-server
  (:require [porteiro.interceptors.user :as interceptors.user]
            [porteiro.diplomatic.http-server.healthy :as diplomatic.http-server.healthy]
            [porteiro.diplomatic.http-server.password :as diplomatic.http-server.password]
            [porteiro.diplomatic.http-server.user :as diplomatic.http-server.user]
            [porteiro.diplomatic.http-server.auth :as diplomatic.http-server.auth]))


(def routes [["/health" :get diplomatic.http-server.healthy/healthy-check :route-name :health-check]
             ["/users" :post [interceptors.user/username-already-in-use-interceptor
                              diplomatic.http-server.user/create-user!] :route-name :create-user]
             ["/users/auth" :post diplomatic.http-server.auth/authenticate-user! :route-name :user-authentication]
             ["/users/password" :put [interceptors.user/auth-interceptor
                                      diplomatic.http-server.password/update-password!] :route-name :password-update]])
