(ns microservice-user-management.interceptors.common
  (:use [clojure pprint])
  (:require [schema.core :as s]
            [io.pedestal.interceptor.error :as error]))

(def error-handler-interceptor
  (error/error-dispatch [ctx ex]
                        [{:exception-type :clojure.lang.ExceptionInfo}]
                        (let [{:keys [status cause reason]} (ex-data ex)]
                          (assoc ctx :response {:status status
                                                :body   {:cause (or cause reason)}}))

                        :else
                        (let []
                          (assoc ctx :response {:status 500 :body (str ex)}))))

(s/defn components-interceptor [system-components]
  {:name  ::components-interceptor
   :enter (fn [context]
            (assoc-in context [:request :components] system-components))})
