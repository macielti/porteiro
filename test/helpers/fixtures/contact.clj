(ns fixtures.contact
  (:require [fixtures.user])
  (:import (java.util Date)))

(def contact-id (random-uuid))
(def chat-id "123456789")

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
   :email   "test@example.com"})

(def datalevin-contact datomic-telegram-contact)