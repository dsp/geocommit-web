(ns worker.main
    (:require [langohr.core :as rmq]
              [langohr.channel :as lch]
              [langohr.queue :as lq]
              [langohr.consumers :as lc]
              [langohr.basic :as lb]))

(def default-exchange-name "")

(defn message-handler
  [channel {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (println (String. payload "UTF-8"))
  (lb/ack channel delivery-tag))

(defn start-consumer
  [connection channel queue-name]
  (.start (Thread. #(lc/subscribe channel queue-name message-handler :auto-ack false))))

(defn -main
    [& args]
    
      (let [connection (rmq/connect)
          channel (lch/open connection)
          queue-name "geocommit.jobs"]
        (println (str "connected " (.getChannelNumber channel)))
        (lq/declare channel queue-name :durable true :exclusive false :auto-delete false)
        (if (= (first args) "fill")
          (do
            (lb/publish channel default-exchange-name queue-name "{\"url\": \"github.com/foo/bar\"}" :content-type "application/json" :type "geocommit.job.init")
            (rmq/close channel)
            (rmq/close connection)) 
          (start-consumer connection channel queue-name))))
