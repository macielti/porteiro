(ns porteiro.controllers.healthy
  (:require [datalevin.core :as d]
            [porteiro.db.datalevin.user :as database.user]
            [porteiro.logic.healthy :as logic.healthy]))

(defn healthy-check
  [datalevin-connection config]
  (let [dependencies {:components [{:component  :config
                                    :is-healthy (try (:jw-token-secret config)
                                                     true
                                                     (catch Exception _ false))}
                                   {:component  :datalevin
                                    :is-healthy (try (database.user/by-username "" (d/db datalevin-connection))
                                                     true
                                                     (catch Exception _ false))}]}]
    (assoc dependencies :is-healthy (logic.healthy/system-healthy? dependencies))))
