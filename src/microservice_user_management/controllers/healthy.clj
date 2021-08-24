(ns microservice-user-management.controllers.healthy
  (:require [microservice-user-management.db.datomic.user :as datomic.user]))

(defn healthy-check
  [datomic config]
  (let [dependencies {:components [{:component  :config
                                    :is-healthy (try (:jw-token-secret config)
                                                     true
                                                     (catch Exception _ false))}
                                   {:component       :datomic
                                    :is-healthy (try (datomic.user/by-username "" datomic)
                                                     true
                                                     (catch Exception _ false))}]}]
    (assoc dependencies :is-healthy (every? :is-healthy
                                            (:components dependencies)))))
