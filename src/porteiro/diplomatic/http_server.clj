(ns porteiro.diplomatic.http-server
  (:require [porteiro.interceptors.user :as interceptors.user]
            [common-clj.io.interceptors :as io.interceptors]
            [porteiro.interceptors.password-reset :as interceptors.password-reset]
            [porteiro.interceptors.user-identity :as interceptors.user-identity]
            [porteiro.diplomatic.http-server.healthy :as diplomatic.http-server.healthy]
            [porteiro.diplomatic.http-server.password :as diplomatic.http-server.password]
            [porteiro.diplomatic.http-server.user :as diplomatic.http-server.user]
            [porteiro.diplomatic.http-server.auth :as diplomatic.http-server.auth]
            [porteiro.diplomatic.http-server.contact :as diplomatic.http-server.contact]
            [porteiro.wire.in.user :as wire.in.user]
            [porteiro.wire.in.auth :as wire.in.auth]
            [porteiro.wire.in.password-reset :as wire.in.password-reset]))


(def routes [["/health" :get diplomatic.http-server.healthy/healthy-check :route-name :health-check]

             ["/users" :post [(io.interceptors/schema-body-in-interceptor wire.in.user/User)
                              interceptors.user/username-already-in-use-interceptor
                              diplomatic.http-server.user/create-user!] :route-name :create-user]

             ["/users/contacts" :get [interceptors.user-identity/user-identity-interceptor
                                      diplomatic.http-server.contact/fetch-contacts] :route-name :fetch-contacts]

             ["/users/auth" :post [(io.interceptors/schema-body-in-interceptor wire.in.auth/UserAuth)
                                   diplomatic.http-server.auth/authenticate-user!] :route-name :user-authentication]

             ["/users/password" :put [(io.interceptors/schema-body-in-interceptor wire.in.user/PasswordUpdate)
                                      interceptors.user-identity/user-identity-interceptor
                                      diplomatic.http-server.password/update-password!] :route-name :password-update]

             ["/users/password-reset" :post [(io.interceptors/schema-body-in-interceptor wire.in.user/PasswordReset)
                                             diplomatic.http-server.password/reset-password!] :route-name :request-password-reset]

             ["/users/password-reset" :put [(io.interceptors/schema-body-in-interceptor wire.in.password-reset/PasswordResetExecution)
                                            interceptors.password-reset/valid-password-reset-execution-token
                                            diplomatic.http-server.password/execute-reset-password!] :route-name :execute-password-reset]

             ["/users/roles" :post [interceptors.user-identity/user-identity-interceptor
                                    (interceptors.user-identity/user-required-roles-interceptor [:admin])
                                    diplomatic.http-server.user/add-role!] :route-name :add-role-to-user]])
