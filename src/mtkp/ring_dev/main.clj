(ns mtkp.ring-dev.main
  (:require
    [clojure.tools.cli :as cli]
    [mtkp.ring-dev.core :as core]))

(defn resolve-var-str
  [var-str]
  (requiring-resolve (symbol var-str)))

(def cli-options
  [["-h" "--help"]
   ["-p" "--port port" "Server port number"
    :default 8000
    :parse-fn #(Long/valueOf %)]
   ["-i" "--init init-fn" "Server initialization function -- called on start up"
    :parse-fn resolve-var-str
    :validate-fn [some?]
    :validate-msg ["Var not found"]]
   ["-d" "--destroy destroy-fn" "Server shutdown function -- called on exit"
    :parse-fn resolve-var-str
    :validate-fn [some?]
    :validate-msg ["var not found"]]
   [nil "--nrepl" "Start an embedded nREPL server alongside the ring server"
    :default nil]
   [nil "--reload-paths reload-paths" "List of source paths to reload on change"
    ;; TODO verify this works
    :default nil
    :parse-fn (fn [dirs] {:dirs dirs})]])

(defn- parse
  [args]
  (let [{:keys [arguments options errors summary]} (cli/parse-opts args cli-options)
        handler-str (first arguments)
        handler (resolve-var-str handler-str)]
    ;; label and validate the server handler
    {:options (assoc options :handler handler)
     :errors (cond->> errors
               (nil? handler) (cons (format "Main handler \"%s\" not found" handler-str)))
     :summary summary}))

(defn -main
  [& args]
  (let [{:keys [options errors summary]} (parse args)]
    (cond
      (:help options) (println summary)
      (seq errors)    (do (run! println errors)
                          (System/exit 1))
      :else           (do (when (:nrepl options)
                            (core/start-nrepl-server))
                          (core/start-jetty-server (:handler options) options)))))
