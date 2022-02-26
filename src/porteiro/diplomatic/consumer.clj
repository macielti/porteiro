(ns porteiro.diplomatic.consumer
  (:require [schema.core :as s]
            [porteiro.adapters.contact :as adapters.contact]
            [porteiro.db.datomic.contact :as datomic.contact]
            [porteiro.wire.in.contact :as wire.in.contact]
            [taoensso.timbre :as timbre]))

(s/defn create-contact!
  [{:keys [message]}
   {:keys [datomic]}]
  (let [contact (adapters.contact/wire->internal-contact message)]
    (datomic.contact/insert! contact (:connection datomic))))

(def topic-consumers
  {:porteiro.create-contact {:schema  wire.in.contact/Contact
                             :handler create-contact!}})
