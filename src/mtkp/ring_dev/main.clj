(ns mtkp.ring-dev.main
  (:require
    [clojure.tools.cli :as cli]))

(defn resolve-var-str
  [var-str]
  (when var-str
    (try
      (requiring-resolve (symbol var-str))
      (catch Exception _))))

(def cli-options
  [[nil "--help" "Print this help"]
   ["-p" "--port port"
    "Server port number"
    :default 8000
    :parse-fn #(Long/valueOf %)]
   [nil "--[no-]reload"
    "Reload source files on change; default true"
    :default true]
   [nil "--reload-path path"
    "Path to source files; can be specified multiple times; default \"src\""
    :assoc-fn (fn [m k v] (update m k conj v))]
   [nil "--browser"
    "Open server endpoint in default system web browser; default false"
    :default false]
   [nil "--stacktrace"
    "Pretty-print stacktraces for uncaught exceptions; default false"
    :default false]
   [nil "--ring-debug"
    "Pretty-print ring requests and responses; default false"
    :default false]
   [nil "--ring-spec"
    "Check ring requests and responses against ring spec; default false"
    :default false]])

(defn- parse
  [args]
  (let [{:keys [arguments options errors summary]} (cli/parse-opts args cli-options)
        handler-str (first arguments)
        handler (resolve-var-str handler-str)]
    {:options (-> options
                  (assoc :handler handler)
                  (update :reload-path not-empty)
                  (update :reload-path (fnil identity ["src"])))
     :errors (->> errors
                  (cons (when (nil? handler)
                          (format "Server handler \"%s\" not found" handler-str)))
                  (remove nil?)
                  (not-empty))
     :summary summary}))

(defn show-help
  "Print help and exit"
  [options-summary]
  (println "Start a ring development server.")
  (println)
  (println "Usage: clj -m mtkp.ring-dev.main [options] handler")
  (println)
  (println "Options:")
  (println options-summary)
  (System/exit 0))

(defn show-errors
  "Print any command errors and exit"
  [errors]
  (run! println errors)
  (System/exit 1))

(defn start-server
  [handler options]
  ((requiring-resolve 'mtkp.ring-dev.core/start-jetty-server) handler options))


(defn -main
  [& args]
  (let [{:keys [options errors summary]} (parse args)]
    (cond
      (:help options) (show-help summary)
      errors          (show-errors errors)
      :else           (start-server (:handler options) options))))
