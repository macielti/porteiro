(ns fixtures.customer
  (:require [clojure.test :refer :all]
            [schema-generators.complete :as c]))

(defonce customer-id (random-uuid))
(def customer-username "manoel-gomes")
(def hashed-password "password-hash")

(def customer (c/complete {:customer/id              customer-id
                           :customer/username        customer-username
                           :customer/hashed-password hashed-password
                           :customer/roles           [:admin :test]} porteiro.models.customer/Customer))

(def postgresql-customer {:id              customer-id
                          :username        customer-username
                          :hashed_password hashed-password})
