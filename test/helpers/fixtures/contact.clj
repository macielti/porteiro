(ns fixtures.contact
  (:require [fixtures.user])
  (:import (java.util UUID Date)))

(def wire-telegram-contact
  {:user-id fixtures.user/wire-user-id
   :type    "telegram"
   :chat-id "123456789"})

(def datomic-telegram-contact
  {:contact/chat-id    "123456789"
   :contact/created-at (Date.)
   :contact/status     :active
   :contact/id         (UUID/randomUUID)
   :contact/type       :telegram
   :contact/user-id    fixtures.user/user-id})

(def wire-email-contact
  {:user-id fixtures.user/wire-user-id
   :type    "email"
   :email   "test@example.com"})
