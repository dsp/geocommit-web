(defproject worker "0.1.0-SNAPSHOT"
  :description "Geocommit background worker"
  :url "http://github.com/dsp/geocommit-web/"
  :license {:name "MIT"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.novemberain/langohr "1.0.0-beta13"]
                 [org.clojure/data.json "0.2.1"]
                 [me.raynes/conch "0.5.0"]
                 [org.clojure/tools.trace "0.7.3"]
                 [com.geocommit/parser "1.0.0"]]
  :plugins [[lein-swank "1.4.5"]]
  :main worker.main)
