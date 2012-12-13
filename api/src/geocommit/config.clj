(ns #^{:doc "HTTP API configuration functions",
       :author "David Soria Parra"}
  geocommit.config
  (:import java.io.File)
  (:use clojure.contrib.logging)
  (:require
    [clojure.contrib.trace :as t]
    [clojure.contrib.java-utils :as jutils]
    [clojure.java.io :as jio]))

(def used-config (atom :development))

(defn config-use
  [config]
  (reset! used-config config))

(defn get-config
  "Get a nested config entry.
   (get-config :foo :bar) will return the {:foo {:bar X}} item."
  [key]
  (let [file (condp = @used-config
                    :production "production.properties"
                    "development.properties")]
    (.getProperty
      (jutils/read-properties
        (File. (.toURI (jio/resource file))))
      key)))
