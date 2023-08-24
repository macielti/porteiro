(ns porteiro.diplomat.http-server.user
  (:require [schema.core :as s]
            [porteiro.controllers.user :as controllers.user]
            [porteiro.adapters.customer :as adapters.user]
            [porteiro.adapters.contact :as adapters.contact])
  (:import (java.util UUID)))

(s/defn create-user!
  [{{:keys [user contact]} :json-params
    {:keys [datalevin]}    :components}]
  (let [{:user/keys [id] :as internal-user} (adapters.user/wire->internal-user user)
        internal-contact (-> contact
                             (assoc :user-id (str id))
                             adapters.contact/wire->internal-contact)]
    (controllers.user/create-user! internal-user internal-contact datalevin)
    {:status 201
     :body   {:user    (adapters.user/internal-user->wire internal-user)
              :contact (adapters.contact/internal->wire internal-contact)}}))

(s/defn add-role!
  [{{wire-user-id :user-id
     wire-role    :role} :query-params
    {:keys [datalevin]}  :components}]
  {:status 200
   :body   (-> (controllers.user/add-role! (UUID/fromString wire-user-id)
                                           (adapters.user/wire->internal-role wire-role)
                                           datalevin)
               adapters.user/internal-user->wire-without-email)})
