(ns porteiro.adapters.contact
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [schema.core :as s]
            [porteiro.models.contact :as models.contact]
            [porteiro.wire.in.contact :as wire.in.contact]
            [porteiro.wire.datomic.user :as wire.datomic.user]
            [porteiro.wire.out.contact :as wire.out.contact]
            [common-clj.time.parser.core :as time.parser])
  (:import (java.util UUID Date)))

(defmulti wire->internal-contact
  (s/fn [{:keys [type]} :- wire.in.contact/Contact]
    (keyword type)))

(s/defmethod wire->internal-contact :email :- models.contact/EmailContact
  [{:keys [user-id email]} :- wire.in.contact/EmailContact]
  {:contact/id         (UUID/randomUUID)
   :contact/user-id    (UUID/fromString user-id)
   :contact/type       :email
   :contact/status     :active
   :contact/email      email
   :contact/created-at (Date.)})

(s/defmethod wire->internal-contact :telegram :- models.contact/TelegramContact
  [{:keys [user-id chat-id]} :- wire.in.contact/TelegramContact]
  {:contact/id         (UUID/randomUUID)
   :contact/user-id    (UUID/fromString user-id)
   :contact/type       :telegram
   :contact/status     :active
   :contact/chat-id    chat-id
   :contact/created-at (Date.)})

(defmulti internal->wire
  (s/fn [{:contact/keys [type]} :- models.contact/Contact]
    type))

(s/defmethod internal->wire :email :- wire.out.contact/EmailContact
  [{:contact/keys [id user-id type status email created-at]} :- models.contact/EmailContact]
  {:id         (str id)
   :user-id    (str user-id)
   :type       (camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING type)
   :status     (camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING status)
   :email      email
   :created-at (time.parser/date->wire created-at)})

(s/defn datomic-user-email->internal-email-contact :- models.contact/EmailContact
  [{:user/keys [id]} :- wire.datomic.user/User
   email :- s/Str]
  {:contact/id         (UUID/randomUUID)
   :contact/user-id    id
   :contact/type       :email
   :contact/status     :active
   :contact/email      email
   :contact/created-at (Date.)})
