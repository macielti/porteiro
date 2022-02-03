(ns porteiro.adapters.contact-test
  (:require [clojure.test :refer :all]
            [schema.test :as s]
            [matcher-combinators.test :refer [match?]]
            [fixtures.contact]
            [porteiro.adapters.contact :as adapters.contact]))

(s/deftest wire->internal-contact
  (testing "that we can internalize a contact"
    (is (match? {:contact/chat-id    "123456789"
                 :contact/created-at inst?
                 :contact/id         uuid?
                 :contact/type       :telegram
                 :contact/user-id    uuid?}
                (adapters.contact/wire->internal-contact fixtures.contact/wire-telegram-contact)))
    (is (match? {:contact/email      "test@example.com"
                 :contact/created-at inst?
                 :contact/id         uuid?
                 :contact/type       :email
                 :contact/user-id    uuid?}
                (adapters.contact/wire->internal-contact fixtures.contact/wire-email-contact)))))
