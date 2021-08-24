(ns microservice-user-management.adapters.healthy)

(defn healthy-check-result->status-code
  [healthy-check-result]
  (if (:is-healthy healthy-check-result)
    200
    503))
