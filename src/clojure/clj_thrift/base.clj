(ns clj-thrift.base
  (:require [clj-thrift.type :as type])
  (:require [clj-thrift.union :as union])
  (:import (java.nio ByteBuffer)))


(defn value
  [object field-name]
  (let [field (type/field (type object) field-name)]
    (.getFieldValue object field)))

(defn value!
  [object field-name value]
  (if (and (type/binary-field? (type object) field-name)
           (not (instance? ByteBuffer value)))
    (recur object field-name (ByteBuffer/wrap value))
    (doto object
      (.setFieldValue
        (type/field (type object) field-name)
        value))))


(defn build
  [type attributes]
  (when attributes
    (if (instance? type attributes)
      attributes
      (reduce (fn [object [field-name value]]
                (value! object
                        field-name
                        (if (type/struct-field? type field-name)
                          (let [sub-type (type/field-type type field-name)]
                            (build sub-type value))
                          value)))
              (.newInstance type)
              attributes))))

; simple data extraction
(defn property-union-value
  "get a value from a union, inside a struct inside a union.
   name is the property name inside the struct.
   Union -> Struct :<field-name> -> Union - value."
  [object field-name]
  (union/current-value (value (union/current-value object) field-name)))

(defn property-value
  "get the named field value from the current structure in the top level union.
   Union -> struct :<field-name> - value."
  [object field-name]
  (value (union/current-value object) field-name))

(defn field-keys
  "Give back an ordered vector of field keys for a struct or union."
  [object]
  (into [] (map keyword (type/ordered-field-names (type object)))))
