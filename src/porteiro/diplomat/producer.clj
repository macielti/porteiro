(ns porteiro.diplomat.producer
  (:require [clostache.parser :as parser]
            [common-clj.integrant-components.sqs-producer :as component.sqs-producer]
            [schema.core :as s])
  (:import (java.util Date)))

(s/defn send-password-reset-notification!
  [password-reset-id :- s/Uuid
   email :- s/Str
   producer]
  (component.sqs-producer/produce! {:queue   "notification"
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
  (component.sqs-producer/produce! {:queue   "notification"
                                    :payload {:email   email
                                              :title   "Authentication Confirmation"
                                              :content (parser/render-resource "templates/authentication_confirmation.mustache"
                                                                               {:moment (Date.)})}}
                                   producer))
