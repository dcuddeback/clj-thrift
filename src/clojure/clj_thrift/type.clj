(ns clj-thrift.type
  "Functions for working with Thrift types."
  (:import (org.apache.thrift TFieldIdEnum)))


(defn- meta-data-map
  "Returns the `metaDataMap` field of Thrift class."
  [#^Class type]
  (.. type
    (getField "metaDataMap")
    (get nil)))


(defn field-ids
  "Returns the set of ID numbers for the fields of a Thrift type. The function's argument should be
  the class itself. It works with structs and unions.

  For the following Thrift IDL,

    struct Name {
      1: string firstName
      2: string lastName
    }

    union ID {
      3: string UUID
      4: i64 hashCode
      5: binary bytes
    }

  this function will return the following:

    (field-ids Name)  ; => #{1 2}
    (field-ids ID)    ; => #{3 4 5}
  "
  [type]
  (into #{} (map (comp #(.getThriftFieldId #^TFieldIdEnum %) key)
                 (meta-data-map type))))
