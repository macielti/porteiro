(ns microservice-user-management.diplomatic.producer
  (:use [clojure pprint])
  (:require [schema.core :as s]
            [microservice-user-management.producer :as producer]
            [clojure.tools.logging :as log]))

(s/defn send-password-reset-notification!
  [password-reset-id :- s/Uuid
   email :- s/Str
   producer]
  (producer/produce! {:topic   :notification
                      :message {:email             email
                                :title             "Password Reset Solicitation"
                                :password-reset-id password-reset-id
                                :content           (str "You requested a password reset operation. \n"
                                                        "The reset password key is valid only for today. \n"
                                                        "Here is your reset password key: " password-reset-id)}} producer)
  (log/info :produce :notification :email email))
