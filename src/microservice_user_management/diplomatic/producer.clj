(ns microservice-user-management.diplomatic.producer
  (:use [clojure pprint])
  (:require [schema.core :as s]
            [microservice-user-management.producer :as producer]
            [clojure.tools.logging :as log]
            [clostache.parser :as parser]))

(s/defn send-password-reset-notification!
  [password-reset-id :- s/Uuid
   email :- s/Str
   producer]
  (producer/produce! {:topic   :notification
                      :message {:email             email
                                :title             "Password Reset Solicitation"
                                :password-reset-id password-reset-id
                                :content           (parser/render-resource "templates/password_reset_solicitation.mustache"
                                                                           {:password-reset-id password-reset-id})}} producer)
  (log/info :produce :notification :email email))
