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

(s/deftest postgresql->contact
  (testing "that we can internalize a email contact entity coming from postgresql database"
    (is (= {:contact/id         fixtures.contact/contact-id
            :contact/user-id    fixtures.user/user-id
            :contact/type       :email
            :contact/email      fixtures.contact/email
            :contact/status     :active
            :contact/created-at fixtures.contact/created-at}
           (adapters.contact/postgresql->internal fixtures.contact/postgres-email-contact))))

  (testing "that we can internalize a telegram contact entity coming from postgresql database"
    (is (= {:contact/id         fixtures.contact/contact-id
            :contact/user-id    fixtures.user/user-id
            :contact/chat-id    fixtures.contact/chat-id
            :contact/type       :telegram
            :contact/status     :active
            :contact/created-at fixtures.contact/created-at}
           (adapters.contact/postgresql->internal fixtures.contact/postgres-telegram-contact)))))
