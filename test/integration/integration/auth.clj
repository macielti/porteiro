(ns integration.auth
  (:require [clojure.test :refer :all]
            [fixtures.user]
            [microservice-user-management.components :as components]
            [integration.aux.http :as http]
            [matcher-combinators.test :refer [match?]]
            [com.stuartsierra.component :as component]))

(deftest auth-test
  (let [system     (components/start-system!)
        service-fn (-> system :server :server :io.pedestal.http/service-fn)
        _          (http/create-user! fixtures.user/user
                                     service-fn)]

    (testing "that users can be authenticated"
      (is (match? {:status 200
                   :body   {:token string?}}
                  (http/auth fixtures.user/user-auth
                             service-fn))))

    (testing "that users can't be authenticated with wrong credentials"
      (is (match? {:status 403
                   :body   {:cause "Wrong username or/and password"}}
                  (http/auth (assoc fixtures.user/user-auth :password "wrong-password")
                             service-fn))))

    (testing "that invalid credential schema input return a nice and readable error"
      (is (match? {:status 422
                   :body   {:cause {:username "missing-required-key"}}}
                  (http/auth {:password "wrong-password"}
                             service-fn))))

    (component/stop system)))
