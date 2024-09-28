(ns porteiro.diplomat.http-server
  (:require [common-clj.io.interceptors :as io.interceptors]
            [porteiro.diplomat.http-server.auth :as diplomat.http-server.auth]
            [porteiro.diplomat.http-server.contact :as diplomat.http-server.contact]
            [porteiro.diplomat.http-server.customer :as diplomat.http-server.user]
            [porteiro.diplomat.http-server.password :as diplomat.http-server.password]
            [porteiro.interceptors.contact :as interceptors.contact]
            [porteiro.interceptors.customer :as interceptors.customer]
            [porteiro.interceptors.customer-identity :as interceptors.user-identity]
            [porteiro.interceptors.password-reset :as interceptors.password-reset]
            [porteiro.wire.in.auth :as wire.in.auth]
            [porteiro.wire.in.customer :as wire.in.customer]
            [porteiro.wire.in.password-reset :as wire.in.password-reset]))

(def routes [["/api/customers" :post [(io.interceptors/schema-body-in-interceptor wire.in.customer/CustomerCreationDocument)
                                      interceptors.customer/username-already-in-use-interceptor
                                      interceptors.contact/email-already-in-use-interceptor
                                      diplomat.http-server.user/create-user!] :route-name :create-customer]

             ["/api/customers/contacts" :get [interceptors.user-identity/customer-identity-interceptor
                                              diplomat.http-server.contact/fetch-contacts] :route-name :fetch-contacts]

             ["/api/customers/auth" :post [(io.interceptors/schema-body-in-interceptor wire.in.auth/CustomerAuth)
                                           diplomat.http-server.auth/authenticate-customer!] :route-name :customer-authentication]

             ["/api/users/password" :put [(io.interceptors/schema-body-in-interceptor wire.in.customer/PasswordUpdate)
                                          interceptors.user-identity/customer-identity-interceptor
                                          diplomat.http-server.password/update-password!] :route-name :password-update]

             ["/api/users/request-password-reset" :post [(io.interceptors/schema-body-in-interceptor wire.in.customer/PasswordReset)
                                                         diplomat.http-server.password/request-reset-password!] :route-name :request-password-reset]

             ["/api/users/password-reset" :post [(io.interceptors/schema-body-in-interceptor wire.in.password-reset/PasswordResetExecution)
                                                 interceptors.password-reset/valid-password-reset-execution-token
                                                 diplomat.http-server.password/reset-password!] :route-name :password-reset]

             ["/api/users/roles" :post [interceptors.user-identity/customer-identity-interceptor
                                        (interceptors.user-identity/customer-required-roles-interceptor [:admin])
                                        diplomat.http-server.user/add-role!] :route-name :add-role-to-user]])
