(ns porteiro.db.datalevin.user
  (:require [datalevin.core :as d]
            [schema.core :as s]
            [porteiro.models.user :as models.user]))

(s/defn insert! :- models.user/User
  [user :- models.user/User
   datalevin-connection]
  (s/validate models.user/User user)
  (d/transact datalevin-connection [user])
  user)