(ns porteiro.admin
  (:require [com.stuartsierra.component :as component]
            [porteiro.diplomat.http-server.customer :as diplomat.http-server.user]
            [medley.core :as medley]
            [porteiro.db.postgres.customer :as database.customer]))

(defrecord Admin [config postgresql]
  component/Lifecycle
  (start [component]
    (let [{{:keys [admin-customer-seed] :as config-content} :config} config
          components (medley/assoc-some {}
                                        :postgresql (:postgresql postgresql)
                                        :config config-content)]

      (when-not (database.customer/by-username (:username (:customer admin-customer-seed)) (:postgresql components))
        (let [wire-customer-id (-> (diplomat.http-server.user/create-user! {:json-params admin-customer-seed
                                                                        :components  components})
                               :body :customer :id)]
          (diplomat.http-server.user/add-role! {:query-params {:customer-id wire-customer-id
                                                               :role    "ADMIN"}
                                                :components   components})))))

  (stop [component]))

(defn new-admin []
  (->Admin {} {}))
