(ns porteiro.v2.admin
  (:require [datomic.api :as d]
            [integrant.core :as ig]
            [porteiro.db.datomic.customer :as database.customer]
            [porteiro.diplomat.http-server.customer :as diplomat.http-server.user]
            [taoensso.timbre :as log]))

(defmethod ig/init-key ::admin
  [_ {:keys [components]}]
  (log/info :starting ::admin)
  (let [{:keys [admin-customer-seed]} (:config components)]
    (when-not (database.customer/by-username (get-in admin-customer-seed [:customer :username]) (-> components :datomic d/db))
      (let [wire-customer-id (-> (diplomat.http-server.user/create-user! {:json-params admin-customer-seed
                                                                          :components  components})
                                 (get-in [:body :customer :id]))]
        (diplomat.http-server.user/add-role! {:query-params {:customer-id wire-customer-id
                                                             :role        "ADMIN"}
                                              :components   components})))))

(defmethod ig/halt-key! ::admin
  [_ _routes]
  (log/info :stopping ::admin))
