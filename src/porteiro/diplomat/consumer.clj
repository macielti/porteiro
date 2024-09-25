(ns porteiro.diplomat.consumer
  (:require [schema.core :as s]
            [porteiro.adapters.contact :as adapters.contact]
            [porteiro.db.datomic.contact :as database.contact]
            [porteiro.wire.in.contact :as wire.in.contact]))

(s/defn create-contact!
  [{contact           :message
    {:keys [datomic]} :components}]
  (let [contact (adapters.contact/wire->internal-contact contact)]
    (database.contact/insert! contact datomic)))

(def consumers
  {"porteiro__create_contact" {:schema     wire.in.contact/Contact
                               :handler-fn create-contact!}})
