(ns porteiro.diplomat.http-server.password
  (:require [schema.core :as s]
            [porteiro.controllers.password-reset :as controllers.password-reset]
            [porteiro.controllers.user :as controllers.user]
            [porteiro.adapters.user :as adapters.user]
            [porteiro.adapters.password-reset :as adapters.password-reset]))

(s/defn reset-password!
  [{password-reset    :json-params
    {:keys [datalevin]} :components}]
  (controllers.password-reset/execute-password-reset! (adapters.password-reset/wire->password-reset-execution-internal password-reset)
                                                      datalevin)
  {:status 204 :body nil})

(s/defn request-reset-password!
  [{password-reset             :json-params
    {:keys [rabbitmq-producer datalevin]} :components}]
  (controllers.user/reset-password! (adapters.user/wire->password-reset-internal password-reset)
                                    rabbitmq-producer
                                    datalevin)
  {:status 202 :body {:message (str "If the email is correct, you should "
                                    "receive a password reset link soon")}})

(s/defn update-password!
  [{password-update            :json-params
    {:user-identity/keys [id]} :user-identity
    {:keys [datalevin]}          :components}]
  (controllers.user/update-password! (adapters.user/wire->password-update-internal password-update)
                                     id
                                     datalevin)
  {:status 204 :body nil})
