(ns mtkp.ring-dev.debug
  (:require
    [clojure.pprint :as pprint]))

(defn wrap-debug
  [handler]
  (fn [request]
    (println "Request trace:")
    (pprint/pprint request)
    (let [response (handler request)]
      (println "Response trace:")
      (pprint/pprint response)
      response)))
