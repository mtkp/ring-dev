(ns mtkp.ring-dev.spec.response
  "Based on lint spec provided in
  https://github.com/ring-clojure/ring/blob/master/ring-devel/src/ring/middleware/lint.clj"
  (:require
    [clojure.spec.alpha :as s]))

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
