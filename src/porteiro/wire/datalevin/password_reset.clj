(ns porteiro.wire.datalevin.password-reset)

(def password-reset-skeleton
  {:password-reset/id {:db/valueType :db.type/uuid
                       :db/unique    :db.unique/identity}})