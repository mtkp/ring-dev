(ns mtkp.ring-dev.spec
  (:require
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [mtkp.ring-dev.spec.response :as response]
    [mtkp.ring-dev.spec.request :as request]))

(defn expound
  "Expound that only prints if x does not pass spec"
  [spec x]
  (when-let [explain-data (s/explain-data spec x)]
    (expound/printer explain-data)
    (flush)))

(defn wrap-spec
  [handler]
  (fn [request]
    (expound ::request/request request)
    (let [response (handler request)]
      (expound ::response/response response)
      response)))
