(ns porteiro.wire.out.contact
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [porteiro.models.contact :as models.contact]
            [schema.core :as s]
            [schema.experimental.abstract-map :as abstract-map]))

(def Type (->> models.contact/types
               (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING)
               (apply s/enum)))

(def Status (->> models.contact/statuses
                 (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING)
                 (apply s/enum)))

(def base-contact
  {:id          s/Str
   :customer-id s/Str
   :type        Type
   :status      Status
   :created-at  s/Str})

(s/defschema Contact (abstract-map/abstract-map-schema :type
                                                       base-contact))

(abstract-map/extend-schema TelegramContact Contact ["TELEGRAM"]
                            {:chat-id s/Str})

(abstract-map/extend-schema EmailContact Contact ["EMAIL"]
                            {:email s/Str})
