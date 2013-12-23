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
  (fact "returns the set of valid field names for unions"
    (type/field-names Identity) => (just #{"name" "ssn"})
    (type/field-names ExampleUnion) => (just #{"foo" "bar" "baz"}))

  (fact "returns the set of valid field ids for structs"
    (type/field-names Name) => (just #{"firstName" "lastName"})
    (type/field-names ExampleStruct) => (just #{"foo" "bar" "baz" "qux"})))
