(ns worker.main-test
  (:use clojure.test
        worker.main))

(deftest init-test
  (is (= nil (init "http://github.com/peritus/geocommit" "github.com/peritus/geocommit"))))

