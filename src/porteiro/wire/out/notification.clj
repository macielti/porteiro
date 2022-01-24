(ns porteiro.wire.out.notification
  (:require [schema.core :as s]))

(s/defschema Notification
  {:topic   s/Str
   :format  (s/enum :html :plain-text)
   :email   s/Str
   :title   s/Str
   :content s/Str})
