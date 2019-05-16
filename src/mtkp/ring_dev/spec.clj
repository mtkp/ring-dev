(ns mtkp.ring-dev.spec
  (:require
    [clojure.spec.alpha :as s]
    [ring.core.spec] ;; load ring specs
    [expound.alpha :as expound]))

(defn expound
  "Expound that only prints if x does not pass spec"
  [spec x]
  (when-let [explain-data (s/explain-data spec x)]
    (expound/printer explain-data)
    (flush)))

(defn wrap-spec
  [handler]
  (fn [request]
    (expound :ring/request ;; request
             ;; workaround for :ring/request query-string spec
             (cond-> request
               (nil? (:query-string request)) (dissoc :query-string)))
    (let [response (handler request)]
      (expound :ring/response response)
      response)))
