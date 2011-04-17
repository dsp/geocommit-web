(defproject geocommit "1.1.0"
  :description "geocommit.com HTTP API"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.6.2"]
                 [oro/oro "2.0.8"]
                 [commons-validator/commons-validator "1.3.1"]]
  :dev-dependencies [[lein-ring "0.4.0"]
                     [swank-clojure "1.2.0"]]
  :aot [geocommit.app_servlet geocommit.services]
  :repositories {"java.net" "http://download.java.net/maven/2"}
  :ring {:handler geocommit.app_servlet/app})
