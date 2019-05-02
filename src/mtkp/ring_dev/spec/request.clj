(ns mtkp.ring-dev.spec.request
  "Based on lint spec provided in
  https://github.com/ring-clojure/ring/blob/master/ring-devel/src/ring/middleware/lint.clj"
  (:require
    [clojure.string :as string]
    [clojure.spec.alpha :as s]))

(defn- lower-case? [s] (= s (string/lower-case s)))

(s/def ::header-key (s/and string? lower-case?))
(s/def ::header-val string?)
(s/def ::headers (s/map-of ::header-key ::header-val))
(s/def ::server-port integer?)
(s/def ::server-name string?)
(s/def ::remote-addr string?)
(s/def ::uri (s/and string? #(string/starts-with? % "/")))
(s/def ::query-string (s/nilable string?))
(s/def ::scheme #{:http :https})
(s/def ::request-method (s/and keyword? (comp lower-case? name)))
(s/def ::protocol string?)
(s/def ::content-type (s/nilable string?))
(s/def ::content-length (s/nilable integer?))
(s/def ::character-encoding (s/nilable string?))
(s/def ::ssl-client-cert (s/nilable (partial instance? java.security.cert.X509Certificate)))
(s/def ::body (s/nilable (partial instance? java.io.InputStream)))

(s/def ::request (s/keys :req-un [::server-port
                                  ::server-name
                                  ::remote-addr
                                  ::uri
                                  ::query-string
                                  ::scheme
                                  ::request-method
                                  ::protocol
                                  ::headers
                                  ::content-type
                                  ::content-length
                                  ::character-encoding
                                  ::ssl-client-cert
                                  ::body]))
