(ns porteiro.wire.datalevin.user)

(def user-skeleton
  {:user/id    {:db/valueType :db.type/uuid
                :db/unique    :db.unique/identity}
   :user/roles {:db/cardinality :db.cardinality/many}})
