(ns mtkp.ring-dev.main
  (:require
    [clojure.tools.cli :as cli]
    [mtkp.ring-dev.core :as core]))

(defn resolve-var-str
  [var-str]
  (requiring-resolve (symbol var-str)))

(defn port
  [raw-port]
  (Long/parseLong raw-port))

(def noop (constantly nil))

(def cli-options
  [["-h" "--help"]
   ["-p" "--port port" "Server port number"
    :default 8000
    :parse-fn #(Long/valueOf %)]
   ["-i" "--init init-fn" "Server initialization function -- called on start up"
    :parse-fn resolve-var-str
    :validate-fn [some?]
    :validate-msg ["Var not found"]]
   ["-d" "--destroy destroy-fn" "Server shutdown function -- called at exit"
    :parse-fn resolve-var-str
    :validate-fn [some?]
    :validate-msg ["var not found"]]
   [nil "--nrepl" "Start an embedded nREPL server alongside the ring server"
    :default nil]
   [nil "--reload-paths reload-paths" "List of source paths to reload on change"
    ;; TODO verify this works
    :default nil
    :parse-fn (fn [dirs] {:dirs dirs})]])

(defn -main
  [& args]
  (let [{:keys [arguments options errors]} (cli/parse-opts args cli-options)
        handler-var-str (first arguments)
        handler (resolve-var-str handler-var-str)
        errors (cond->> errors
                 (nil? handler) (cons (format "Main handler \"%s\" not found"
                                              handler-var-str)))]
    (when (seq errors)
      (run! println errors)
      (System/exit 1))
    (when (:nrepl options)
      (core/start-nrepl-server))
    (core/start-jetty-server handler options)))
