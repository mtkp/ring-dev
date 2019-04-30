(ns mtkp.ring-dev.core
  (:require
    [clojure.java.io :as io]
    [nrepl.server :as nrepl]
    [ring.adapter.jetty :as jetty]
    [ring.middleware.reload :as reload]))

(defn- add-shutdown-hook!
  [f]
  (.addShutdownHook (Runtime/getRuntime) (Thread. f)))

(defn start-nrepl-server
  []
  (let [{:keys [port] :as server} (nrepl/start-server)]
    (add-shutdown-hook! (partial nrepl/stop-server server))
    (println (format "Started embedded nREPL server on port %s" port))
    (doto (io/file ".nrepl-port")
      (spit port)
      (.deleteOnExit))))

(defn start-jetty-server 
  [app-handler user-options]
  (let [handler (-> app-handler
                    (reload/wrap-reload (:reload-paths user-options)))
        options (-> user-options
                    (select-keys [:port])
                    (assoc :join? false))
        {:keys [init destroy]} user-options]
    (when init
      (init))
    (when destroy
      (add-shutdown-hook! destroy))
    (let [server (jetty/run-jetty handler options)]
      (println (format "Started ring server on port %s" (:port options)))
      (.join server))))
