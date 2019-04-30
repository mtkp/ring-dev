(ns mtkp.ring.app)

(def counter (atom nil))

(defn my-init
  []
  (reset! counter 0))

(defn my-destroy
  []
  (reset! counter nil))

(defn count! [] (swap! counter inc))

(defn my-handler
  [request]
  (count!)
  {:status 200
   :body (format "counter is [%s]" @counter)})
