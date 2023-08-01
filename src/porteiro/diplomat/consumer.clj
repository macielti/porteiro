(ns porteiro.diplomat.consumer
  (:require [schema.core :as s]
            [porteiro.adapters.contact :as adapters.contact]
            [porteiro.db.datalevin.contact :as database.contact]
            [porteiro.wire.in.contact :as wire.in.contact]))

(s/defn create-contact!
  [{:keys [payload]}
   {:keys [datalevin]}]
  (let [contact (adapters.contact/wire->internal-contact payload)]
    (database.contact/insert! contact datalevin)))

(def topic-consumers
  {:porteiro.create-contact {:schema  wire.in.contact/Contact
                             :handler-fn create-contact!}})
