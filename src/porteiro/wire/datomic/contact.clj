(ns porteiro.wire.datomic.contact)

(def contact
  [{:db/ident       :contact/id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Contact Id"}
   {:db/ident       :contact/user-id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc         "User id that owns this contact entity"}
   {:db/ident       :contact/type
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc         "Type of the contact (:telegram, :email)"}
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

(def contact-update-passport
  [{:db/ident       :contact-update-passport/id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Contact Update Passport Id"}
   {:db/ident       :contact-update-passport/contact-id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc         "Contact id that this Contact Update Passport is referring to"}
   {:db/ident       :contact-update-passport/created-at
    :db/valueType   :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc         "Instant emission of the Contact Update Passport"}
   {:db/ident       :contact-update-passport/state
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc         "State of the token (:open, :closed)"}])
