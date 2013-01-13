(ns leiningen.libdir
  (:use
    [clojure.pprint])
  (:require
    [clojure.java.io :as io]
    [leiningen.jar :as jar]
    [leiningen.core.project :as project]
    [leiningen.core.main :as main]
    [leiningen.core.classpath :as classpath]))

(defn- copy-files
  [target-dir files]
  (doseq [file files]
    (let [target-file (java.io.File. target-dir (.getName file))]
      (io/copy file target-file))))

(defn copy-deps
  [project target-dir sourceify?]
  (let [project (project/unmerge-profiles project [:default])
        deps (->> (classpath/resolve-dependencies :dependencies project)
                  (filter #(and (.endsWith (.getName %) ".jar") (.exists %))))]
    (.mkdirs target-dir)
    (copy-files target-dir deps)
    (main/info "Copied" (count deps) "file(s) to:" (.getAbsolutePath target-dir))))

(defn libdir
  "Copy jar dependencies into the project lib directory.

Set :libdir-path in the project to a string containing the relative path
of the target lib dir.  If omitted, the default is \"lib\"."
  [project & args]
  (let [target-dir (if-let [path (:libdir-path project)]
                     (java.io.File. path)
                     (java.io.File. (:root project) "lib"))]
    (copy-deps project target-dir nil)))

