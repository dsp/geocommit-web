(ns worker.main
    (:require [langohr.core :as rmq]
              [langohr.channel :as lch]
              [langohr.queue :as lq]
              [langohr.consumers :as lc]
              [langohr.basic :as lb]))

(def default-exchange-name "")

(defn message-handler
  [channel {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (println (str type (String. payload "UTF-8")))
  (lb/ack channel delivery-tag))

(defn start-consumer
  [connection channel queue-name handler-fn]
  (.start (Thread. #(lc/subscribe channel queue-name handler-fn :auto-ack false))))

(defn -main
    [& args]
    
      (let [connection (rmq/connect)
          channel-fast (lch/open connection)
          channel-slow (lch/open connection)]
        (println (str "connected " (.getChannelNumber channel-fast)))
        (lq/declare channel-fast "geocommit.job.fast" :durable true :exclusive false :auto-delete false)
        (lq/declare channel-fast "geocommit.job.slow" :durable true :exclusive false :auto-delete false)
        (if (= (first args) "fill")
          (do
            (lb/publish
              channel-fast
              default-exchange-name
              "geocommit.job.fast"
              "{\"url\": \"github.com/already/exists\"}"
              :content-type "application/json"
              :type "geocommit.job.update")
            (lb/publish
              channel-slow
              default-exchange-name
              "geocommit.job.slow"
              "{\"url\": \"github.com/new/repo\"}"
              :content-type "application/json"
              :type "geocommit.job.init")
            (lb/publish
              channel-fast
              default-exchange-name
              "geocommit.job.fast" "{\"url\": \"github.com/new/repo\"}"
              :content-type "application/json"
              :type "geocommit.job.update")
            (rmq/close channel-fast)
            (rmq/close channel-slow)
            (rmq/close connection)) 
          (do
            (start-consumer connection channel-fast "geocommit.job.fast" message-handler)
            (start-consumer connection channel-slow "geocommit.job.slow" message-handler)))))
