(ns fixtures.contact
  (:import (java.util UUID)))

(def wire-telegram-contact
  {:user-id (str (UUID/randomUUID))
   :type    "telegram"
   :chat-id "123456789"})

(def wire-email-contact
  {:user-id (str (UUID/randomUUID))
   :type    "email"
   :email   "test@example.com"})
