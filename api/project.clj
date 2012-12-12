(defproject geocommit "1.1.1"
  :description "geocommit.com HTTP API"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "1.1.3"]
                 [oro/oro "2.0.8"]
                 [commons-validator/commons-validator "1.3.1"]]
  :plugins [[lein-ring "0.7.5"]]
  :resources-path "resources"
;  :aot [geocommit.app_servlet geocommit.services]
  :repositories {"java.net" "http://download.java.net/maven/2"}
  :ring {:handler geocommit.app_servlet/app})
