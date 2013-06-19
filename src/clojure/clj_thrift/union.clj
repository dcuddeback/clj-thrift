(ns clj-thrift.union
  "Functions for working with Thrift unions."
  (:require [clojure.core.incubator :refer [.?.]]))

(defn current-field-id
  "Returns the ID of a union's currently set field. For the following union, the return value will be
  `1` if `bar` is set or `2` if `qux` is set:

    union Foo {
      1: string bar
      2: double qux
    }

  If no field is set on the union, `nil` will be returned."
  [union]
  (.?. union
    (getSetField)
    (getThriftFieldId)))
