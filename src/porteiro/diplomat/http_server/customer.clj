(ns porteiro.diplomat.http-server.customer
  (:require [medley.core :as medley]
            [porteiro.adapters.contact :as adapters.contact]
            [porteiro.adapters.customer :as adapters.customer]
            [porteiro.controllers.customer :as controllers.customer]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn create-customer!
  [{{:keys [customer contact]} :json-params
    {:keys [datomic]}          :components}]
  (let [{:customer/keys [id] :as customer'} (adapters.customer/wire->internal-customer customer)
        contact' (some-> contact
                         (assoc :customer-id (str id))
                         adapters.contact/wire->internal-contact)]
    (controllers.customer/create-customer! customer' contact' datomic)
    {:status 201
     :body   (medley/assoc-some {:customer (adapters.customer/internal-customer->wire customer')}
                                :contact (some-> contact'
                                                 adapters.contact/internal->wire))}))

(s/defn add-role!
  [{{wire-customer-id :customer-id
     wire-role        :role} :query-params
    {:keys [datomic]}        :components}]
  {:status 200
   :body   (-> (controllers.customer/add-role! (UUID/fromString wire-customer-id)
                                               (adapters.customer/wire->internal-role wire-role)
                                               datomic)
               adapters.customer/internal-customer->wire-without-email)})
