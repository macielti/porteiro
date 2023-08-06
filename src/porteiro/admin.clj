(ns porteiro.admin
  (:require [com.stuartsierra.component :as component]
            [datalevin.core :as d]
            [porteiro.diplomat.http-server.user :as diplomat.http-server.user]
            [medley.core :as medley]
            [porteiro.db.datalevin.user :as database.user]))

(defrecord Admin [config datalevin]
  component/Lifecycle
  (start [component]
    (let [{{:keys [admin-user-seed] :as config-content} :config} config
          components (medley/assoc-some {}
                                        :datalevin (:datalevin datalevin)
                                        :config config-content)]

      (when-not (database.user/by-username (:username (:user admin-user-seed)) (-> components :datalevin d/db))
        (let [wire-user-id (-> (diplomat.http-server.user/create-user! {:json-params admin-user-seed
                                                                        :components  components})
                               :body :user :id)]
          (diplomat.http-server.user/add-role! {:query-params {:user-id wire-user-id
                                                               :role    "ADMIN"}
                                                :components   components})))))

  (stop [component]))

(defn new-admin []
  (->Admin {} {}))
