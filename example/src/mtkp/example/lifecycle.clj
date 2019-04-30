(ns mtkp.example.lifecycle)

(defn initialize
  []
  (println "(lifecycle) Starting up server..."))

(defn destroy
  []
  (println "(lifecycle) Shutting down server..."))
