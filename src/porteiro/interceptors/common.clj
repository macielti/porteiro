(ns porteiro.interceptors.common
  (:require [schema.core :as s]
            [io.pedestal.interceptor.error :as error]
            [clojure.tools.logging :as log]))

(def error-handler-interceptor
  (error/error-dispatch [ctx ex]
                        [{:exception-type :clojure.lang.ExceptionInfo}]
                        (let [{:keys [status cause reason]} (ex-data ex)]
                          (assoc ctx :response {:status status
                                                :body   {:cause (or cause reason)}}))

                        :else
                        (do (log/error ex)
                            (assoc ctx :response {:status 500 :body nil}))))

(s/defn components-interceptor [system-components]
  {:name  ::components-interceptor
   :enter (fn [context]
            (assoc-in context [:request :components] system-components))})
