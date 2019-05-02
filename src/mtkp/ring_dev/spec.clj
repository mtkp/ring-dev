(ns mtkp.ring-dev.spec
  (:require
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]))

(s/def ::body (s/or :nil nil?
                    :string string?
                    :seq seq?
                    :file (partial instance? java.io.File)
                    :input-stream (partial instance? java.io.InputStream)))

(s/def ::header-val (s/or :string string?
                          :string-coll (s/coll-of string?)))

(s/def ::headers (s/map-of string? ::header-val))

(s/def ::status (s/and integer? #(>= % 100)))

(s/def ::response (s/keys :req-un [::status]
                          :opt-un [::body ::headers]))

(defn expound
  "Expound that does not print if success."
  [spec x]
  (when-let [explain-data (s/explain-data spec x)]
    (expound/printer explain-data)
    (flush)))

(defn wrap-spec
  [handler]
  (fn [request]
    (let [response (handler request)]
      (expound ::response response)
      response)))
