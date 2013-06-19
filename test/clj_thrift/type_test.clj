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
