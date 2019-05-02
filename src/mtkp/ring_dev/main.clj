(ns mtkp.ring-dev.main
  (:require
    [clojure.tools.cli :as cli]
    [mtkp.ring-dev.core :as core]))

(defn resolve-var-str
  [var-str]
  (cond-> var-str
    (string? var-str) (symbol)
    true (requiring-resolve)))

(def cli-options
  [["-h" "--help"]
   ["-p" "--port port"
    "Server port number"
    :default 8000
    :parse-fn #(Long/valueOf %)]
   [nil "--browser"
    "Open server endpoint in default system web browser"
    :default false]
   [nil "--reload"
    "Reload source files on change"
    :default true]
   [nil "--reload-path path"
    "Path to reloadable source files; can be specified multiple times; default \"src\""
    :assoc-fn (fn [m k v] (update m k conj v))]
   [nil "--stacktrace"
    "Pretty-print stacktraces for uncaught exceptions in the handler"
    :default false]
   [nil "--ring-debug"
    "Pretty-print ring requests and responses"
    :default false]
   [nil "--ring-spec"
    "Check server responses against ring spec"
    :default false]])

(defn- parse
  [args]
  (let [{:keys [arguments options errors summary]} (cli/parse-opts args cli-options)
        handler-str (first arguments)
        handler (resolve-var-str handler-str)]
    {:options (-> options
                  (assoc :handler handler)
                  (assoc :reload-path (or (seq (:reload-path options)) ["src"])))
     :errors (->> errors
                  (cons (when (nil? handler)
                          (format "Main handler \"%s\" not found" handler-str)))
                  (remove nil?))
     :summary summary}))

(defn -main
  [& args]
  (let [{:keys [options errors summary]} (parse args)]
    (cond
      (:help options) (println summary)
      (seq errors)    (do (run! println errors)
                          (System/exit 1))
      :else           (core/start-jetty-server (:handler options) options))))
