(ns porteiro.adapters.common
  (:require [camel-snake-kebab.core :refer [->kebab-case]]
            [schema.core :as s]))

(s/defn str->keyword-kebab-case :- s/Keyword
  [k :- s/Str]
  (-> k
      ->kebab-case
      keyword))
