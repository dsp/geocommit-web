(ns worker.repository
    (:require [me.raynes.conch :refer [programs with-programs let-programs]]))

(defprotocol Repository
  (clone [this]))

(deftype GitHub [id url]
  Repository
  (clone
    [this]
    (programs git)
    (git "clone" url)))

(deftype BitBucket [id url]
  Repository
  (clone
    [this]
    nil))

(defmulti create-repo
          (fn [id url]
              (or
                (and (.startsWith id "bitbucket.org") ::bitbucket)
                (and (.startsWith id "github.com") ::github))))

(defmethod create-repo ::github [id url] (GitHub. id url))
(defmethod create-repo ::bitbucket [id url] (BitBucket. id url))
(defmethod create-repo ::default [id url] nil)
