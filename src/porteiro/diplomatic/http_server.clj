(ns porteiro.diplomatic.http-server
  (:require [porteiro.diplomatic.http-server.healthy :as diplomatic.http-server.healthy]))


(def routes [["/health" :get diplomatic.http-server.healthy/healthy-check :route-name :health-check]])
