(ns porteiro.diplomat.http-server.customer
  (:require [schema.core :as s]
            [porteiro.controllers.customer :as controllers.customer]
            [porteiro.adapters.customer :as adapters.customer]
            [porteiro.adapters.contact :as adapters.contact])
  (:import (java.util UUID)))

(s/defn create-user!
  [{{:keys [customer contact]} :json-params
    {:keys [postgresql]}       :components}]
  (let [{:customer/keys [id] :as internal-customer} (adapters.customer/wire->internal-customer #p customer)
        internal-contact (-> contact
                             (assoc :customer-id (str id))
                             adapters.contact/wire->internal-contact)]
    (controllers.customer/create-customer! internal-customer internal-contact postgresql)
    {:status 201
     :body   {:customer (adapters.customer/internal-customer->wire internal-customer)
              :contact  (adapters.contact/internal->wire internal-contact)}}))

(s/defn add-role!
  [{{wire-customer-id :customer-id
     wire-role    :role} :query-params
    {:keys [postgresql]}  :components}]
  {:status 200
   :body   (-> (controllers.customer/add-role! (UUID/fromString wire-customer-id)
                                               (adapters.customer/wire->internal-role wire-role)
                                               postgresql)
               adapters.customer/internal-customer->wire-without-email)})
