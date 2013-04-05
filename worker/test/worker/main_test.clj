(ns worker.main-test
  (:use clojure.test
        worker.main))

(deftest init-test
  (is (= nil (init "http://github.com/peritus/geocommit" "github.com/peritus/geocommit"))))

(deftest work-dispatcher-test
  (let [repo-url "http://github.com/peritus/geocommit"
        repo-id "github.com/peritus/geocommit"]
    (with-redefs [init (fn
                    [id url]
                    (and (is (= id repo-id)) (is (= url repo-url))))
                  update (fn
                    [id url commits]
                    (and (is (= id repo-id)) (is (= url repo-url))) (is (= commits ["commit1"])))
                  langohr.basic/ack (fn [channel ^long tag] true)]
      (is (= true (work-dispatcher
                    nil
                    {:content-type "application/json" :delivery-tags 123 :type "geocommit.job.init"}
                    (.getBytes (str "{\"repository-url\": \"" repo-url "\", \"identifier\": \"" repo-id "\"}")))))
      (is (= true (work-dispatcher
                    nil
                    {:content-type "application/json" :delivery-tags 123 :type "geocommit.job.update"}
                    (.getBytes (str "{\"repository-url\": \"" repo-url "\", \"identifier\": \"" repo-id "\", \"commits\": [\"commit1\"]}"))))))))
