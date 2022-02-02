(ns porteiro.models.contact
  (:require [schema.core :as s])
  (:import (java.util Date)))

(s/defschema ContactUpdatePassport
  {:contact-update-passport/id         s/Uuid
   :contact-update-passport/contact-id s/Uuid
   :contact-update-passport/created-at Date
   :contact-update-passport/state      (s/enum :open :closed)})
