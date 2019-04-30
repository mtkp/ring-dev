(ns mtkp.ring-dev.nrepl
  (:require
    [clojure.java.io :as io]
    [nrepl.server :as nrepl]
    [mtkp.ring-dev.core :as core]))

(defn- nrepl-handler
  []
  (require 'cider.nrepl)
  (ns-resolve 'cider.nrepl 'cider-nrepl-handler))

(defn start
  []
  (let [port (some-> (System/getProperty "mtkp.ring-dev.nrepl.port")
                     (Long/valueOf))
        {:keys [port] :as server} (nrepl/start-server :handler (nrepl-handler)
                                                      :port port)]
    (core/add-shutdown-hook! (partial nrepl/stop-server server))
    (doto (io/file ".nrepl-port")
      (spit port)
      (.deleteOnExit))
    (println (format "Started embedded nREPL server on port [%s]" port))))
