(ns porteiro.interceptors.user
  (:require [clojure.string :as str]
            [porteiro.db.datomic.user :as datomic.user]
            [porteiro.db.datomic.session :as datomic.session]
            [porteiro.adapters.auth :as adapters.auth]))

(def username-already-in-use-interceptor
  {:name  ::user-already-in-use-interceptor
   :enter (fn [{{{:keys [username] :or {username ""}} :json-params
                 {:keys [datomic]}                    :components} :request :as context}]
            (let [user (datomic.user/by-username username (:connection datomic))]
              (if-not (empty? user)
                (throw (ex-info "Username already in use" {:status 409
                                                           :cause  "username already in use by other user"}))))
            context)})

(def auth-interceptor
  {:name  ::auth-interceptor
   :enter (fn [{{{:keys [datomic]} :components
                 headers           :headers} :request :as context}]
            (assoc-in context [:request :user-identity]
                      (let [jw-token (-> (get headers "authorization")
                                         (str/split #" ")
                                         last)
                            {:keys [id]} (adapters.auth/decoded-jwt jw-token)
                            {:session/keys [secret]} (datomic.session/valid-session-by-user-id id (:connection datomic))]
                        (adapters.auth/jwt-wire->internal jw-token (str secret)))))})
