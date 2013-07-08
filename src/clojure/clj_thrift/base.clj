(ns clj-thrift.base
  (:require [clj-thrift.type :as type])
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
