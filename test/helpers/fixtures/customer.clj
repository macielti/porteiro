(ns fixtures.customer
  (:require [clojure.test :refer :all]
            [schema-generators.complete :as c]))

(def customer-id (random-uuid))
(def customer-username "manoel-gomes")
(def customer (c/complete {:customer/id              customer-id
                           :customer/username        customer-username
                           :customer/hashed-password "password-hash"
                           :customer/roles           [:admin :test]} porteiro.models.customer/Customer))