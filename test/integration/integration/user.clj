(ns integration.user
  (:require [clojure.test :refer :all]
            [integration.aux.http :as http]
            [matcher-combinators.test :refer [match?]]
            [com.stuartsierra.component :as component]
            [microservice-user-management.components :as components]))

(def user {:username "IDoNotCare"
           :email    "example@example.com"
           :password "some-strong-password"})

(deftest create-user-test
  (let [system     (components/start-system!)
        service-fn (-> system :server :server :io.pedestal.http/service-fn)]

    (testing "that users can be created"
      (is (match? {:status 201
                   :body   {:id       string?
                            :username "IDoNotCare"
                            :email    "example@example.com"}}
                  (http/create-user user
                                    service-fn))))

    (testing "that username must be unique"
      (is (= {:status 409
              :body   {:cause "username already in use by other user"}}
             (http/create-user user
                               service-fn))))

    (testing "request body must respect the schema"
      (is (= {:status 422, :body {:cause {:username "missing-required-key"}}}
             (http/create-user (dissoc user :username)
                               service-fn))))

    (component/stop system)))
