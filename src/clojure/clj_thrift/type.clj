(ns clj-thrift.type
  "Functions for working with Thrift types."
  (:require [clojure.zip :as zip])
  (:import (org.apache.thrift TFieldIdEnum)))


(defn- meta-data-map
  "Returns the `metaDataMap` field of Thrift class."
  [#^Class type]
  (.. type
    (getField "metaDataMap")
    (get nil)))


(defn- field-enum-map
  [type]
  (into {} (map (juxt (comp keyword #(.getFieldName %))
                      identity)
                (keys (meta-data-map type)))))

(defn- field-meta-map
  [type]
  (into {} (map (juxt (comp keyword #(.getFieldName %) key)
                      val)
                (meta-data-map type))))

(defn- field-meta
  [type field-name]
  (get (field-meta-map type) field-name))

(defn- field-value
  [type field-name]
  (.valueMetaData (field-meta type field-name)))


(defn field
  [type field-name]
  (get (field-enum-map type) field-name))

(defn field-type
  [type field-name]
  (.structClass (field-value type field-name)))

(defn struct-field?
  [type field-name]
  (.isStruct (field-value type field-name)))

(defn container-field?
  [type field-name]
  (.isContainer (field-value type field-name)))

(defn binary-field?
  [type field-name]
  (.isBinary (field-value type field-name)))

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


(defn field-names
  "Returns the set of names for the fields of a Thrift type. The function's argument should be
  the class itself. It works with structs and unions.
  "
  [type]
  (into #{} (map (comp #(.getFieldName #^TFieldIdEnum %) key)
                 (meta-data-map type))))

; get ordered field names and ids to facilitate Creation of Cascalog taps and operators.
(defn ordered-field-names
  "Returns a vector of names for the fields of a Thrift type. The function's argument should be
  the class itself. It works with structs and unions.
  "
  [type]
  "Give an ordered vector of field names for a struct or union."
  (into [] (map (comp #(.getFieldName #^TFieldIdEnum %) key)
                 (meta-data-map type))))

(defn ordered-field-ids
  "Give an ordered vector of field ids for a struct or union."
  [type]
  (into [] (map (comp #(.getThriftFieldId #^TFieldIdEnum %) key)
                 (meta-data-map type))))

(defn field-ids-names
  "Create vector of field vectors where a field vector is [ id name]."
  [type]
  (into [] (mapv vector (ordered-field-ids type) (ordered-field-names type))))


;; Get a list of property paths to make it easy to create
;; Cascalog taps for partitioned data.
(defn- get-type
  "get field type for a data type and field name if it's a struct or union
   field name vector is as created by field-ids-names [ id name ]."
  [parent field-name-vector]
  (let [field-key (keyword (last field-name-vector))]
    (if (struct-field? parent field-key)
      (field-type parent field-key))))

(defn get-field-tree
  "drill down through the fields of a thrift object and create
   a tree of paths with field name field id pairs.
   Each leaf node terminating in nil"
  [datatype & {:keys [p] :or []}]
  (if datatype
    (mapv #(vec (conj p %1 (get-field-tree (get-type %2 %1) :p (vec (conj p %1)))))
         (field-ids-names datatype) (repeat datatype))))

(defn- ptest
  "if it's a vector that ends with nil, it's a leaf node"
  [x]
    (if (and (vector? x) (nil? (last x))) true false))

(defn- get-property-paths
  "get the property leaf nodes out of the field tree"
  [tree]
    (loop [loc (zip/vector-zip tree)
           ps []]
      (if (zip/end? loc)
        ps
        (recur (zip/next loc)
               (if (ptest (zip/node loc))
                 (conj ps (keep identity (zip/node loc)))
                 ps)))))

(defn property-paths
  "Get a list of property paths for a thrift data type.
   Each row consists of a set of field id, name pairs, leading
   to a field, such that a path can be created for a property similar
   to the way a Pail Partitioner does."
  [type]
  (get-property-paths (mapv #(vec (reverse %)) (get-field-tree type))))
