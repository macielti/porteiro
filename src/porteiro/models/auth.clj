(ns porteiro.models.auth
  (:require [schema.core :as s]))

(s/defschema UserAuth
  #:user-auth {:username s/Str
               :password s/Str})
