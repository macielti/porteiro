(ns fixtures.contact
  (:require [fixtures.user])
  (:import (java.util Date)))

(def contact-id (random-uuid))
(def chat-id "123456789")

(def email "test@example.com")

(def wire-telegram-contact
  {:user-id fixtures.user/wire-user-id
   :type    "telegram"
   :chat-id chat-id})

(def datomic-telegram-contact
  {:contact/chat-id    chat-id
   :contact/created-at (Date.)
   :contact/status     :active
   :contact/id         contact-id
   :contact/type       :telegram
   :contact/user-id    fixtures.user/user-id})

(def wire-email-contact
  {:user-id fixtures.user/wire-user-id
   :type    "email"
   :email   email})

(def datalevin-telegram-contact datomic-telegram-contact)

(def datalevin-email-contact
  {:contact/created-at (Date.)
   :contact/status     :active
   :contact/id         contact-id
   :contact/type       :email
   :contact/email      email
   :contact/user-id    fixtures.user/user-id})
