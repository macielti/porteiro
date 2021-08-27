(ns fixtures.user)

(def user {:username "ednaldo-pereira"
           :email    "example@example.com"
           :password "some-strong-password"})

(def user-auth (dissoc user :email))

(def password-update {:oldPassword (:password user)
                      :newPassword "new-strong-password"})
