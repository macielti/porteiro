(ns porteiro.wire.in.contact
  (:require [schema.core :as s]
            [schema.experimental.abstract-map :as abstract-map]))

;TODO: Refactor it to get the value from internal model definition (source of truth)
(def Type (s/enum "email" "telegram"))

(def base-contact
  {:customer-id s/Str
   :type    Type})

(s/defschema Contact (abstract-map/abstract-map-schema :type
                                                       base-contact))

(abstract-map/extend-schema TelegramContact Contact ["telegram"]
                            {:chat-id s/Str})

(abstract-map/extend-schema EmailContact Contact ["email"]
                            {:email s/Str})

(s/defschema ContactWithoutCustomerId (abstract-map/abstract-map-schema :type
                                                                        (dissoc base-contact :customer-id)))

(abstract-map/extend-schema TelegramContactWithoutUserId ContactWithoutCustomerId ["telegram"]
                            {:chat-id s/Str})

(abstract-map/extend-schema EmailContactWithoutUserId ContactWithoutCustomerId ["email"]
                            {:email s/Str})