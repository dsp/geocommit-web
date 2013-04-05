(ns worker.main
    (:require [langohr.core :as rmq]
              [langohr.channel :as lch]
              [langohr.queue :as lq]
              [langohr.consumers :as lc]
              [langohr.basic :as lb]
              [clojure.data.json :as json]))

(def default-exchange-name "")

(defn init
  [repo-id, repo-url]
  (println (str "init: " repo-id repo-url)))

(defn update
  [repo-id, repo-url, commits]
  (println (str "update: " repo-id repo-url commits)))

(defn work-dispatcher
  [channel {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (let [job  (json/read-str (String. payload "UTF-8") :key-fn keyword)]
    (condp = type
      "geocommit.job.init" (init (:identifier job) (:repository-url job))
      "geocommit.job.update" (update (:identifier job) (:repository-url job) (:commits job)))
  (lb/ack channel delivery-tag)))

(defn start-worker
  [connection channel queue-name worker-fn]
  (.start (Thread. #(lc/subscribe channel queue-name worker-fn :auto-ack false))))

(defn -main
  [& args]
  (let [connection (rmq/connect)
        channel-default (lch/open connection)
        channel-slow (lch/open connection)]
    (println (str "connected " (.getChannelNumber channel-default)))
    (lq/declare channel-default "geocommit.job.fast" :durable true :exclusive false :auto-delete false)
    (lq/declare channel-default "geocommit.job.slow" :durable true :exclusive false :auto-delete false)
    (if (= (first args) "fill")
      (do
        (lb/publish
          channel-default
          default-exchange-name
          "geocommit.job.slow"
          "{\"repository-url\": \"https://github.com/peritus/geocommit\", \"identifier\": \"github.com/peritus/geocommit\"}"
          :content-type "application/json"
          :type "geocommit.job.init")
        (lb/publish
          channel-default
          default-exchange-name
          "geocommit.job.fast" "{\"repository-url\": \"https://github.com/peritus/geocommit\", \"identifier\": \"github.com/peritus/geocommit\", \"commits\": [\"8b66817de64bea338726a1d479f42e28957bd337\", \"6b3fb2040331889642eb35f708c3c2e56dbc1ea5\"]}"
          :content-type "application/json"
          :type "geocommit.job.update")
        (rmq/close channel-default)
        (rmq/close channel-slow)
        (rmq/close connection)) 
      (do
        (start-worker connection channel-default "geocommit.job.fast" work-dispatcher)
        (start-worker connection channel-slow "geocommit.job.slow" work-dispatcher)))))
