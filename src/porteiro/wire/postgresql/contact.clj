(ns porteiro.wire.postgresql.contact
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [porteiro.models.contact :as models.contact]
            [schema.core :as s]
            [schema.experimental.abstract-map :as abstract-map]))

(def types (-> (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING models.contact/types)
               set))
(def Type (apply s/enum types))

(def statuses (-> (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING models.contact/statuses)
                  set))
(def Status (apply s/enum statuses))

(def base-contact
  {:id         s/Uuid
   :user_id    s/Uuid
   :type       Type
   :status     Status
   :created_at s/Inst})

(s/defschema Contact (abstract-map/abstract-map-schema :type
                                                       base-contact))

(abstract-map/extend-schema TelegramContact Contact ["TELEGRAM"]
                            {:chat_id s/Str})

(abstract-map/extend-schema EmailContact Contact ["EMAIL"]
                            {:email s/Str})