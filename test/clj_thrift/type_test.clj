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

(facts "ordered-field-ids"
  (fact "returns the set of valid field ids for unions"
    (type/field-ids Identity) => (just [1 2])
    (type/field-ids ExampleUnion) => (just [3 5 6]))

  (fact "returns the set of valid field ids for structs"
    (type/field-ids Name) => (just [1 2])
    (type/field-ids ExampleStruct) => (just [3 4 5 7])))

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

(facts "field-ids-names"
  (fact "returns a vector of valid field ids and names for structs or unions"
    (type/field-ids-names Identity) => (just [[1 "name"] [2 "ssn"]])
    (type/field-ids-names ExampleUnion) => (just [[3 "foo"] [5 "bar"] [6 "baz"]]))

  (fact "returns a vector of valid field ids and names for structs or unions"
    (type/field-ids-names Name) => (just [[1 "firstName"] [2 "lastName"]])
    (type/field-ids-names ExampleStruct) => (just [[3 "foo"] [4 "bar"] [5 "baz"] [7 "qux"]])))

; I can't figure out how to make this one work. It would be nice if it did.
#_(facts "property-paths"
  (fact "returns a vector of field paths for structs or unions"
    (type/property-paths Identity) => (just [([1 "name"] [1 "firstName"]) ([1 "name"] [2 "lastName"]) ([2 "ssn"])])
    (type/property-paths ExampleUnion) => (just [([3 "foo"]) ([5 "bar"]) ([6 "baz"])]))

  (fact "returns a vector of field paths for structs or unions"
    (type/property-paths Name) => (just  [([1 "firstName"]) ([2 "lastName"])])
    (type/property-paths ExampleStruct) => (just [([3 "foo"]) ([4 "bar"]) ([5 "baz"]) ([7 "qux"])])))

(facts "property-paths"
  (fact "returns a vector of valid field pathsfor structs or unions"
    (first (type/property-paths Identity)) => (just [[1 "name"] [1 "firstName"]])
    (second (type/property-paths Identity)) => (just [[1 "name"] [2 "lastName"]])
    (last (type/property-paths Identity)) => (just [[2 "ssn"]])

    (first (type/property-paths ExampleUnion)) => (just [[3 "foo"]])
    (second (type/property-paths ExampleUnion)) => (just [[5 "bar"]])
    (last (type/property-paths ExampleUnion)) => (just [[6 "baz"]]))

  (fact "returns a vector of valid paths for structs or unions"
    (first (type/property-paths Name)) => (contains  [[1 "firstName"]])
    (last (type/property-paths Name)) => (contains  [[2 "lastName"]])

    (first (type/field-ids-names ExampleStruct)) => (just [3 "foo"])
    (second (type/field-ids-names ExampleStruct)) => (just [4 "bar"])
    (nth (type/field-ids-names ExampleStruct) 2) => (just [5 "baz"])
    (last  (type/field-ids-names ExampleStruct)) => (just [7 "qux"])))
