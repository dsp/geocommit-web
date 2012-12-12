(defproject geocommit "1.1.1"
  :description "geocommit.com HTTP API"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "1.1.3"]
                 [oro/oro "2.0.8"]
                 [commons-validator/commons-validator "1.3.1"]
                 [commons-configuration/commons-configuration "1.9"]]
  :plugins [[lein-ring "0.7.5"]]
;  :aot [geocommit.app_servlet geocommit.services]
  :profiles {
    :dev {:ring {:init geocommit.app_servlet/dev}}
    :production {:ring {:init geocommit.app_servlet/production}}
  }
  :repositories {"java.net" "http://download.java.net/maven/2"}
  :filespecs [{:type :paths :paths ["config/dev" "config/production"]}]
  :ring {
    :handler geocommit.app_servlet/app
    :resources-path "resources/"
    :war-resources-path "config/dev/"
  })
