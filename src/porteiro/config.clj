(ns porteiro.config
  (:require [porteiro.adapters.common :as adapters.common]
            [com.stuartsierra.component :as component]
            [cheshire.core :as json]))

(defrecord Config []
  component/Lifecycle
  (start [this]
    (let [config (json/parse-string (slurp "resources/config.json")
                                    adapters.common/str->keyword-kebab-case)]
      (assoc this :config config)))

  (stop [this]
    (assoc this :config nil)))

(defn new-config []
  (->Config))
