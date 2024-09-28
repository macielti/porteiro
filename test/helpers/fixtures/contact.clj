(ns fixtures.contact
  (:require [fixtures.customer])
  (:import (java.util Date)))

(def contact-id (random-uuid))
(def chat-id "123456789")

(def email "test@example.com")

(def wire-telegram-contact
  {:customer-id (str fixtures.customer/customer-id)
   :type    "telegram"
   :chat-id chat-id})

#_(def datomic-telegram-contact
    {:contact/chat-id    chat-id
     :contact/created-at (Date.)
     :contact/status     :active
     :contact/id         contact-id
     :contact/type       :telegram
     :contact/user-id    fixtures.user/user-id})

(def wire-email-contact
  {:customer-id (str fixtures.customer/customer-id)
   :type    "email"
   :email   email})

#_(def datalevin-telegram-contact datomic-telegram-contact)

#_(def datalevin-email-contact
    {:contact/created-at (Date.)
     :contact/status     :active
     :contact/id         contact-id
     :contact/type       :email
     :contact/email      email
     :contact/user-id    fixtures.user/user-id})

;---PostgreSQL---
(def created-at (Date.))
(def contact
  {:contact/chat-id     chat-id
   :contact/created-at  created-at
   :contact/status      :active
   :contact/id          contact-id
   :contact/type        :telegram
   :contact/customer-id fixtures.customer/customer-id})

(def email-contact
  (-> (assoc contact :contact/type :email
             :contact/email email)
      (dissoc :contact/chat-id)))

#_(def postgres-telegram-contact
    {:chat_id    chat-id
     :created_at created-at
     :status     "ACTIVE"
     :id         contact-id
     :type       "TELEGRAM"
     :user_id    fixtures.user/user-id})

#_(def postgres-email-contact
    {:created_at created-at
     :status     "ACTIVE"
     :id         contact-id
     :type       "EMAIL"
     :email      email
     :user_id    fixtures.user/user-id})
