(ns porteiro.diplomat.producer
  (:require [schema.core :as s]
            [clostache.parser :as parser]
            [common-clj.component.rabbitmq.producer :as rabbitmq.producer]
            [porteiro.wire.datomic.customer :as wire.datomic.user])
  (:import (java.util Date)))

(s/defn send-password-reset-notification!
  [password-reset-id :- s/Uuid
   email :- s/Str
   producer]
  (rabbitmq.producer/produce! {:topic   :notification
                               :payload {:email             email
                                         :title             "Password Reset Solicitation"
                                         :password-reset-id password-reset-id
                                         :content           (parser/render-resource "templates/password_reset_solicitation.mustache"
                                                                                    {:password-reset-id password-reset-id})}}
                              producer))

(s/defn send-success-auth-notification!
  "Should be used to notify users on every successful authentication"
  [email :- s/Str
   producer]
  (rabbitmq.producer/produce! {:topic   :notification
                               :payload {:email   email
                                         :title   "Authentication Confirmation"
                                         :content (parser/render-resource "templates/authentication_confirmation.mustache"
                                                                          {:moment (Date.)})}}
                              producer))

(s/defn create-email-contact!
  [{:user/keys [id]} :- wire.datomic.user/User
   email :- s/Str
   producer]
  (rabbitmq.producer/produce! {:topic   :porteiro.create-contact
                               :payload {:user-id (str id)
                                         :type    :email
                                         :email   email}}
                              producer))
