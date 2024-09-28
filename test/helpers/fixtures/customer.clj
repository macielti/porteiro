(ns fixtures.customer
  (:require [clojure.test :refer :all]
            [porteiro.models.customer :as models.customer]
            [schema-generators.complete :as c]))

(defonce customer-id (random-uuid))
(def customer-username "manoel-gomes")
(def hashed-password "password-hash")
(def password "some-strong-password")

(def customer (c/complete {:customer/id              customer-id
                           :customer/username        customer-username
                           :customer/hashed-password hashed-password
                           :customer/roles           [:admin :test]} models.customer/Customer))

(def wire-email-contact-for-user-creation {:type  "email"
                                           :email "test@example.com"})

(def wire-customer-auth {:username customer-username
                         :password password})

(def wire-customer-creation {:customer wire-customer-auth
                             :contact  wire-email-contact-for-user-creation})

(def password-update {:oldPassword password
                      :newPassword "new-strong-password"})

(def wire-admin-customer-auth {:username "admin"
                               :password "very-strong-password"})
