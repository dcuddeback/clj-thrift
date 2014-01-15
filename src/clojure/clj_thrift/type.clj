(ns clj-thrift.type
  "Functions for working with Thrift types."
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

(defn- field-value
  [type field-name]
  (.valueMetaData
    (get (field-meta-map type)
         field-name)))

(defn field
  [type field-name]
  (get (field-enum-map type) field-name))

(defn field-meta
  "Returns a map describing the metadata of the Thrift type's named field."
  [type field-name]
  (when-let [f (field type (keyword field-name))]
    {:id   (.getThriftFieldId #^TFieldIdEnum f)
     :name field-name  }))

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
    (field-ids ID)    ; => #{3 4 5}"
  [type]
  (into #{} (map (comp #(.getThriftFieldId #^TFieldIdEnum %) key)
                 (meta-data-map type))))

(defn field-names
  "Returns the set of names for the fields of a Thrift type. The function's argument should be
  the class itself. It works with structs and unions."
  [type]
  (into #{} (map (comp #(.getFieldName #^TFieldIdEnum %) key)
                 (meta-data-map type))))

(defn field-meta-list
  "Returns a list of metadata for each field of a given Thrift type."
  [type]
  (map (partial field-meta type)
       (field-names type)))
