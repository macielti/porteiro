(ns porteiro.admin
  (:require [com.stuartsierra.component :as component]
            [porteiro.diplomat.http-server.user :as diplomat.http-server.user]
            [medley.core :as medley]
            [porteiro.db.datomic.user :as database.user]))

(defrecord Admin [config datomic]
  component/Lifecycle
  (start [component]
    (let [{{:keys [admin-user-seed] :as config-content} :config} config
          components (medley/assoc-some {}
                                        :datomic (:datomic datomic)
                                        :config config-content)]

      (when-not (database.user/by-username (:username admin-user-seed) (-> components :datomic :connection))
        (let [wire-user-id (-> (diplomat.http-server.user/create-user! {:json-params  admin-user-seed
                                                                          :components components})
                               :body :user :id)]
          (diplomat.http-server.user/add-role! {:query-params {:user-id wire-user-id
                                                                 :role  "ADMIN"}
                                                  :components components})))))

  (stop [component]))

(defn new-admin []
  (->Admin {} {}))
