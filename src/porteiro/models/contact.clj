(ns porteiro.models.contact
  (:require [schema.core :as s]
            [schema.experimental.abstract-map :as abstract-map]))

(def types #{:email :telegram})

(def Type (apply s/enum types))

(def statuses #{:active :deactivated})

(def Status (apply s/enum statuses))

(def base-contact
  {:contact/id          s/Uuid
   :contact/customer-id s/Uuid
   :contact/type        Type
   :contact/status      Status
   :contact/created-at  s/Inst})

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
