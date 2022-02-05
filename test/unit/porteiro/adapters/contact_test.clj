(ns porteiro.adapters.contact-test
  (:require [clojure.test :refer :all]
            [schema.test :as s]
            [matcher-combinators.test :refer [match?]]
            [fixtures.contact]
            [porteiro.adapters.contact :as adapters.contact]))

(s/deftest wire->internal-contact
  (testing "that we can internalize a contact"
    (is (match? {:contact/user-id    uuid?
                 :contact/id         uuid?
                 :contact/type       :telegram
                 :contact/chat-id    "123456789"
                 :contact/status     :active
                 :contact/created-at inst?}
                (adapters.contact/wire->internal-contact fixtures.contact/wire-telegram-contact)))
    (is (match? {:contact/user-id    uuid?
                 :contact/id         uuid?
                 :contact/type       :email
                 :contact/email      "test@example.com"
                 :contact/status     :active
                 :contact/created-at inst?}
                (adapters.contact/wire->internal-contact fixtures.contact/wire-email-contact)))))
