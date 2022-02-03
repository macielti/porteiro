(ns porteiro.diplomatic.producer
  (:require [schema.core :as s]
            [common-clj.component.kafka.producer :as kafka.producer]
            [clostache.parser :as parser]
            [porteiro.wire.datomic.user :as wire.datomic.user]
            [porteiro.models.contact :as models.contact]
            [medley.core :as medley])
  (:import (java.util Date)))

(s/defn send-password-reset-notification!
  [password-reset-id :- s/Uuid
   email :- s/Str
   producer]
  (kafka.producer/produce! {:topic   :notification
                            :message {:email             email
                                      :title             "Password Reset Solicitation"
                                      :password-reset-id password-reset-id
                                      :content           (parser/render-resource "templates/password_reset_solicitation.mustache"
                                                                                 {:password-reset-id password-reset-id})}} producer))

(s/defn send-success-auth-notification!
  "Should be used to notify users on every successful authentication"
  [email :- s/Str
   producer]
  (kafka.producer/produce! {:topic   :notification
                            :message {:email   email
                                      :title   "Authentication Confirmation"
                                      :content (parser/render-resource "templates/authentication_confirmation.mustache"
                                                                       {:moment (Date.)})}} producer))

(s/defn create-email-contact!
  [{:user/keys [id]} :- wire.datomic.user/User
   email :- s/Str
   producer]
  (kafka.producer/produce! {:topic   :porteiro/create-contact
                            :message {:user-id id
                                      :type    :email
                                      :email   email}}
                           producer))
