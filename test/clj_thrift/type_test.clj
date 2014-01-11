(ns clj-thrift.type-test
  (:require [clj-thrift.type :as type])
  (:import (clj_thrift.fakes Name Identity ExampleStruct ExampleUnion))
  (:use midje.sweet))


(facts "field-ids"
  (fact "returns the set of valid field ids for unions"
    (type/field-ids Identity) => (just #{1 2})
    (type/field-ids ExampleUnion) => (just #{3 5 6}))

  (fact "returns the set of valid field ids for structs"
    (type/field-ids Name) => (just #{1 2})
    (type/field-ids ExampleStruct) => (just #{3 4 5 7})))

(facts "field-names"
  (fact "returns the set of valid field names for structs or unions"
    (type/field-names Identity) => (just #{"name" "ssn"})
    (type/field-names ExampleUnion) => (just #{"foo" "bar" "baz"}))

  (fact "returns the set of valid field ids for structs"
    (type/field-names Name) => (just #{"firstName" "lastName"})
    (type/field-names ExampleStruct) => (just #{"foo" "bar" "baz" "qux"})))

(facts "ordered-field-names"
  (fact "returns a vector of valid field names for structs or unions"
    (type/ordered-field-names Identity) => (just ["name" "ssn"])
    (type/ordered-field-names ExampleUnion) => (just ["foo" "bar" "baz"]))

  (fact "returns a vector of valid field ids for structs"
    (type/ordered-field-names Name) => (just ["firstName" "lastName"])
    (type/ordered-field-names ExampleStruct) => (just ["foo" "bar" "baz" "qux"])))

(facts "field"
    (fact "returns a field's id and name"
       (type/field Identity "name") => (just {:id 1 :name "name" })
       (type/field Identity "ssn") => (just {:id 2 :name  "ssn"})
       (type/field ExampleStruct "foo") => (just {:id 3 :name "foo"})
       (type/field ExampleStruct "bar") => (just {:id 4 :name  "bar"})
       (type/field ExampleStruct "baz") => (just {:id 5 :name "baz"})
       (type/field ExampleStruct "qux") => (just {:id 7 :name "qux"})))

(facts "fields-map"
    (fact "returns a map of field id's and names for a structure or union"
          (first (type/field-map Identity)) => (just {:id 1 :name "name" })
          (last  (type/field-map Identity)) => (just {:id 2 :name  "ssn"})
          (nth  (type/field-map ExampleStruct ) 0) => (just {:id 3 :name "foo" } )
          (nth  (type/field-map ExampleStruct ) 1) => (just {:id 4 :name  "bar"} )
          (nth  (type/field-map ExampleStruct ) 2) => (just {:id 5 :name "baz"} )
          (nth  (type/field-map ExampleStruct ) 3) => (just {:id 7 :name "qux"})))
