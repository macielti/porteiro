(ns porteiro.adapters.contact
  (:require [schema.core :as s]
            [porteiro.models.contact :as models.contact]
            [porteiro.wire.in.contact :as wire.in.contact])
  (:import (java.util UUID Date)))

(defmulti wire->internal-contact
          (fn [{:keys type} :- wire.in.contact/Contact]
            (keyword type)))

(s/defmethod wire->internal-contact :email :- models.contact/EmailContact
             [{:keys [user-id email]}]
             {:contact/id         (UUID/randomUUID)
              :contact/user-id    (UUID/fromString user-id)
              :contact/type       :email
              :contact/email      email
              :contact/created-at (Date.)})

(s/defmethod wire->internal-contact :telegram :- models.contact/TelegramContact
             [{:keys [user-id chat-id]}]
             {:contact/id         (UUID/randomUUID)
              :contact/user-id    (UUID/fromString user-id)
              :contact/type       :telegram
              :contact/email      chat-id
              :contact/created-at (Date.)})
