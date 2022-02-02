(ns porteiro.db.datomic.config
  (:require [porteiro.wire.datomic.user :as wire.datomic.user]
            [porteiro.wire.datomic.contact :as wire.datomic.contact]
            [porteiro.wire.datomic.password-reset :as wire.datomic.password-reset]
            [porteiro.wire.datomic.session :as wire.datomic.session]))

(def schemas (concat []
                     wire.datomic.user/user
                     wire.datomic.contact/contact
                     wire.datomic.contact/contact-update-passport
                     wire.datomic.password-reset/password-reset
                     wire.datomic.session/session))
