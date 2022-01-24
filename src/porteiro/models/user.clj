(ns porteiro.models.user
  (:require [schema.core :as s]))

(s/defschema PasswordUpdate
  "Schema for password update"
  {:old-password s/Str
   :new-password s/Str})
