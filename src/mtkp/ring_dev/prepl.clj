(ns mtkp.ring-dev.prepl
  (:require
    [clojure.core.server :as server]
    [mtkp.ring-dev.core :as core]))

(def server-name ::prepl)

(defn start
  [port]
  (server/start-server {:accept 'clojure.core.server/io-prepl
                        :name server-name
                        :port port})
  (core/add-shutdown-hook! (partial server/stop-server server-name))
  (println (format "Started embedded prepl server on port [%s]" port)))
