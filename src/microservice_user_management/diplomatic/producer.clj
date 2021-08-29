(ns microservice-user-management.diplomatic.producer
  (:require [schema.core :as s]
            [microservice-user-management.producer :as producer]))

(s/defn send-notification!
  [{:keys [email title content]}
   producer]
  (producer/produce! {:topic   "NOTIFICATION"
                      :message {:email   email
                                :title   title
                                :content content}}
                     producer))
