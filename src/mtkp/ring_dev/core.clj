(ns mtkp.ring-dev.core
  (:require
    [clojure.java.browse :as browse]
    [ring.adapter.jetty :as jetty]
    [ring.middleware.stacktrace :as stacktrace]
    [ring.middleware.reload :as reload]
    [mtkp.ring-dev.debug :as debug]
    [mtkp.ring-dev.spec :as spec])
  (:import
    (org.eclipse.jetty.server Server ServerConnector)))

(defn server-endpoint
  [^Server server]
  (let [^ServerConnector conn (first (.getConnectors server))
        host (or (.getHost conn) "localhost")
        port (.getLocalPort conn)]
    (format "http://%s:%s" host port)))

(defn start-jetty-server
  [user-handler user-options]
  (let [handler (cond-> user-handler
                  (:ring-debug user-options) (debug/wrap-debug)
                  (:ring-spec user-options) (spec/wrap-spec)
                  (:reload user-options) (reload/wrap-reload {:dirs (:reload-path user-options)})
                  (:stacktrace user-options) (stacktrace/wrap-stacktrace))
        options (-> user-options
                    (select-keys [:port])
                    (assoc :join? false))
        server (jetty/run-jetty handler options)
        endpoint (server-endpoint server)]
    (println (format "Started ring jetty server at %s" endpoint))
    (when (:browser user-options)
      (browse/browse-url endpoint))
    (.join server)))
