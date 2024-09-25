(ns porteiro.wire.datomic.contact
  (:require [porteiro.models.contact :as models.contact]))

(def contact
  [{:db/ident       :contact/id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Contact Id"}
   {:db/ident       :contact/customer-id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc         "Customer id that owns this contact entity"}
   {:db/ident       :contact/type
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc         "Type of the contact (:telegram, :email)"}
   {:db/ident       :contact/status
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc         "Status of the contact (:active, :deactivated). One user can only have one contact active by type"}
   {:db/ident       :contact/created-at
    :db/valueType   :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc         "Instant emission of the Contact"}
   {:db/ident       :contact/chat-id
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Telegram chat id"}
   {:db/ident       :contact/email
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Email"}])

(def Contact models.contact/Contact)
