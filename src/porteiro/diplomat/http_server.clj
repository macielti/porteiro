(ns porteiro.diplomat.http-server
  (:require [porteiro.interceptors.user :as interceptors.user]
            [common-clj.io.interceptors :as io.interceptors]
            [porteiro.interceptors.password-reset :as interceptors.password-reset]
            [porteiro.interceptors.user-identity :as interceptors.user-identity]
            [porteiro.diplomat.http-server.healthy :as diplomat.http-server.healthy]
            [porteiro.diplomat.http-server.password :as diplomat.http-server.password]
            [porteiro.diplomat.http-server.user :as diplomat.http-server.user]
            [porteiro.diplomat.http-server.auth :as diplomat.http-server.auth]
            [porteiro.diplomat.http-server.contact :as diplomat.http-server.contact]
            [porteiro.wire.in.user :as wire.in.user]
            [porteiro.wire.in.auth :as wire.in.auth]
            [porteiro.wire.in.password-reset :as wire.in.password-reset]))


(def routes [["/api/health" :get diplomat.http-server.healthy/healthy-check :route-name :health-check]

             ["/api/users" :post [(io.interceptors/schema-body-in-interceptor wire.in.user/UserCreationDocument)
                                  interceptors.user/username-already-in-use-interceptor-datalevin
                                  interceptors.user/email-already-in-use-interceptor-datalevin
                                  diplomat.http-server.user/create-user!] :route-name :create-user]

             ["/api/users/contacts" :get [interceptors.user-identity/user-identity-interceptor
                                          diplomat.http-server.contact/fetch-contacts] :route-name :fetch-contacts]

             ["/api/users/auth" :post [(io.interceptors/schema-body-in-interceptor wire.in.auth/UserAuth)
                                       {:name  ::user-authentication
                                        :enter (fn [context]
                                                 (assoc context :response (diplomat.http-server.auth/authenticate-user! (:request context))))}] :route-name :user-authentication]

             ["/api/users/password" :put [(io.interceptors/schema-body-in-interceptor wire.in.user/PasswordUpdate)
                                          interceptors.user-identity/user-identity-interceptor
                                          diplomat.http-server.password/update-password!] :route-name :password-update]

             ["/api/users/request-password-reset" :post [(io.interceptors/schema-body-in-interceptor wire.in.user/PasswordReset)
                                                         diplomat.http-server.password/request-reset-password!] :route-name :request-password-reset]

             ["/api/users/password-reset" :post [(io.interceptors/schema-body-in-interceptor wire.in.password-reset/PasswordResetExecution)
                                                 interceptors.password-reset/valid-password-reset-execution-token
                                                 diplomat.http-server.password/reset-password!] :route-name :password-reset]

             ["/api/users/roles" :post [interceptors.user-identity/user-identity-interceptor
                                        (interceptors.user-identity/user-required-roles-interceptor [:admin])
                                        diplomat.http-server.user/add-role!] :route-name :add-role-to-user]])
