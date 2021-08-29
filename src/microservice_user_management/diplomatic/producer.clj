(ns microservice-user-management.diplomatic.producer
  (:use [clojure pprint])
  (:require [schema.core :as s]
            [microservice-user-management.producer :as producer]))

(s/defn send-notification!
  [password-reset-id :- s/Uuid
   email :- s/Str
   producer]
  (producer/produce! {:topic   :notification
                      :message {:email   email
                                :title   "Password Reset Solicitation"
                                :content (str "You request the password reset operation. "
                                              "Here is your reset-password key: " password-reset-id)}} producer))
