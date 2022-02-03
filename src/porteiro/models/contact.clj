(ns porteiro.models.contact
  (:require [schema.core :as s]
            [schema.experimental.abstract-map :as abstract-map]))

(def Type (s/enum :email :telegram))

(def base-contact
  {:contact/id         s/Uuid
   :contact/user-id    s/Uuid
   :contact/type       Type
   :contact/created-at s/Inst})

(s/defschema Contact (abstract-map/abstract-map-schema :contact/type
                                                       base-contact))

(abstract-map/extend-schema TelegramContact Contact [:telegram]
  {:contact/chat-id s/Str})

(abstract-map/extend-schema EmailContact Contact [:email]
  {:contact/email s/Str})

(s/defschema ContactUpdatePassport
  {:contact-update-passport/id         s/Uuid
   :contact-update-passport/contact-id s/Uuid
   :contact-update-passport/created-at s/Inst
   :contact-update-passport/state      (s/enum :open :closed)})
