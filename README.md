# lein-libdir

A Leiningen 2.0 plugin for copying dependencies into a "lib/" folder in your project.

Unlike Leiningen 1.x, Leiningen 2.x doesn't copy your dependency libs into your project's "lib" folder;
instead it builds the classpath to reference the libs from your local Maven .m2 repository.

Sometimes it is handy to have the jars in your project though.  Perhaps you like to check jars into your
source control, or perhaps you use Clojure within a Java project that has different build conventions.

This plugin provides a Leiningen task to emulate the behaviour of Leiningen 1.x.

## Usage

Put `[lein-libdir "0.1.0"]` into the `:plugins` vector of your
`:user` profile`, or reference it from the :plugins property of your project.

The following options can be specified under the `:libdir` key in the
project definition:

* `:path` - A string containing the relative path of the target lib
  dir. If omitted, the default is "lib".
* `filter` - A regular expression which must match on file names of jar
  file names to be copied or a function which should be a predicate
  accepting java.io.File objects and returning whether it should be
  copied or not. The default is to copy all files.

## Example

```clojure
(defproject myproject "0.1.0-SNAPSHOT"
  :plugins [[lein-libdir "0.1.0"]]
  :libdir {:path "lib"
           :filter #"^frob"})
```

To copy the files, run the libdir task:

    $ lein libdir

## Notes

The target directory is not automatically cleaned before running this task.

## License

Copyright © 2013 David Powell

Distributed under the Eclipse Public License, the same as Clojure.
