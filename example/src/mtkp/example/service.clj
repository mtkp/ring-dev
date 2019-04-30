(ns mtkp.example.service
  (:require
    [muuntaja.core :as muuntaja]
    [reitit.coercion.spec :as coercion-spec]
    [reitit.ring :as ring]
    [reitit.ring.coercion :as ring-coercion]
    [reitit.ring.middleware.exception :as ring-exception]
    [reitit.ring.middleware.muuntaja :as ring-muuntaja]
    [reitit.ring.middleware.parameters :as ring-parameters]))

(def x (atom 0))

(defn with-request-tracing
  [handler]
  (fn [request]
    (println (format "(service/trace) request: [%s]" request))
    (handler request)))

(defn with-monitor
  [handler]
  (fn [request]
    (let [start (System/currentTimeMillis)
          response (handler request)]
      (println (format "(service/monitor) path = %s | method = %s | status = %s | latency = %s"
                       (:uri request)
                       (:request-method request)
                       (:status response)
                       (- (System/currentTimeMillis) start)))
      response)))

(def service-api
  (ring/ring-handler
    (ring/router
      [["/x" {:get {:responses {200 {:body {:message string?}}}
                     :handler (fn [_]
                                {:status 200
                                 :body {:message (format "[%s]" @x)}})}}]
       ["/hello" {:get {:parameters {:query {:name string?}}
                        :responses {200 {:body {:message string?}}}
                        :handler (fn [req]
                                   {:status 200
                                    :body {:message (->> (get-in req [:parameters :query :name])
                                                         (format "Hello, %s!"))}})}}]]
      {:data {:coercion coercion-spec/coercion
              :muuntaja muuntaja/instance
              :middleware [ring-parameters/parameters-middleware
                           ring-muuntaja/format-negotiate-middleware
                           ring-muuntaja/format-response-middleware
                           ring-exception/exception-middleware
                           ring-muuntaja/format-request-middleware
                           ring-coercion/coerce-request-middleware
                           ring-coercion/coerce-response-middleware]}})))

(def handler
  (-> service-api
      (with-request-tracing)
      (with-monitor)))
