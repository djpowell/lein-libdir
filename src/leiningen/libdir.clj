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
  [project target-dir filter-pred sourceify?]
  (let [project (project/unmerge-profiles project [:default])
        deps (->> (classpath/resolve-dependencies :dependencies project)
                  (filter #(and (.endsWith (.getName %) ".jar") (.exists %)))
                  (filter filter-pred))]
    (.mkdirs target-dir)
    (copy-files target-dir deps)
    (main/info "Copied" (count deps) "file(s) to:" (.getAbsolutePath target-dir))))

(defn file-matches [re file]
  (re-find re (str file)))

(defn libdir
  "Copy jar dependencies into the project lib directory.

The following options can be specified under the :libdir key in the
project definition:

  :path   - A string containing the relative path of the target lib
            dir. If omitted, the default is \"lib\".
  :filter - A regular expression which must match on file names of jar
            file names to be copied or a function which should be a predicate
            accepting java.io.File objects and returning whether it should be
            copied or not. The default is to copy all files."
  [project & args]
  (let [options (:libdir project)
        target-dir  (if-let [path (or (:path options) (:libdir-path project))]
                      (java.io.File. path)
                      (java.io.File. (:root project) "lib"))
        filter-pred (let [filter (:filter options)]
                      (cond (fn? filter) filter
                            (instance? java.util.regex.Pattern filter)
                            (partial file-matches filter)
                            (not filter) identity))]
    (copy-deps project target-dir filter-pred nil)))

