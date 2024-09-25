(ns porteiro.db.datomic.config
  (:require [porteiro.wire.datomic.customer :as wire.datomic.customer]
            [porteiro.wire.datomic.contact :as wire.datomic.contact]
            [porteiro.wire.datomic.password-reset :as wire.datomic.password-reset]
            [porteiro.wire.datomic.session :as wire.datomic.session]))

(def schemas (concat []
                     wire.datomic.customer/customer
                     wire.datomic.contact/contact
                     wire.datomic.password-reset/password-reset
                     wire.datomic.session/session))
