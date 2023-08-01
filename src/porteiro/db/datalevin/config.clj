(ns porteiro.db.datalevin.config
  (:require [porteiro.wire.datalevin.user :as wire.datalevin.user]
            [porteiro.wire.datalevin.password-reset :as wire.datalevin.password-reset]))

(def schema (merge {}
                   wire.datalevin.user/user-skeleton
                   wire.datalevin.password-reset/password-reset-skeleton))