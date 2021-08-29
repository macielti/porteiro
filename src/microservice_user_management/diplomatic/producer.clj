(ns microservice-user-management.diplomatic.producer
  (:require [schema.core :as s]
            [microservice-user-management.producer :as producer]))

(s/defn send-notification!
  [email
   reset-link
   producer]
  (producer/produce! {:topic   "NOTIFICATION"
                      :message {:email   email
                                :title   "Password Reset Solicitation"
                                :content (str "")}} producer))
