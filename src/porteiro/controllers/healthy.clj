(ns porteiro.controllers.healthy
  (:require [porteiro.db.datomic.user :as datomic.user]
            [porteiro.logic.healthy :as logic.healthy]))

(defn healthy-check
  [datomic config]
  (let [dependencies {:components [{:component  :config
                                    :is-healthy (try (:jw-token-secret config)
                                                     true
                                                     (catch Exception _ false))}
                                   {:component  :datomic
                                    :is-healthy (try (datomic.user/by-username "" datomic)
                                                     true
                                                     (catch Exception _ false))}]}]
    (assoc dependencies :is-healthy (logic.healthy/system-healthy? dependencies))))
