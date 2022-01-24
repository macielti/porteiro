(ns porteiro.adapters.session
  (:require [schema.core :as s]
            [porteiro.wire.datomic.session :as wire.datomic.session])
  (:import (java.util UUID Date)))

(s/defn ->datomic :- wire.datomic.session/Session
  [user-id jw-token-secret]
  #:session{:session/id         (UUID/randomUUID)
            :session/user-id    user-id
            :session/secret     jw-token-secret
            :session/valid?     true
            :session/created-at (Date.)})
