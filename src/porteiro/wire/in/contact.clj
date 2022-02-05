(ns porteiro.wire.in.contact
  (:require [schema.core :as s]
            [schema.experimental.abstract-map :as abstract-map]))

(def Type (s/enum "email" "telegram"))                      ;TODO: Refactor it to get the value from internal model definition (source of truth)

(def base-contact
  {:user-id s/Str
   :type    Type})

(s/defschema Contact (abstract-map/abstract-map-schema :type
                                                       base-contact))

(abstract-map/extend-schema TelegramContact Contact ["telegram"]
  {:chat-id s/Str})

(abstract-map/extend-schema EmailContact Contact ["email"]
  {:email s/Str})
