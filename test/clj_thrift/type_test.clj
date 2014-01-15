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

(facts "field-meta"
  (fact "returns a field's id"
    (type/field-meta Identity "name") => (contains {:id 1 })
    (type/field-meta Identity "ssn")  => (contains {:id 2})
    (type/field-meta ExampleStruct "foo") => (contains {:id 3})
    (type/field-meta ExampleStruct "bar") => (contains {:id 4})
    (type/field-meta ExampleStruct "baz") => (contains {:id 5})
    (type/field-meta ExampleStruct "qux") => (contains {:id 7}))

  (fact "returns a field's name"
    (type/field-meta Identity "name") => (contains {:name "name" })
    (type/field-meta Identity "ssn")  => (contains {:name "ssn"})
    (type/field-meta ExampleStruct "foo") => (contains {:name "foo"})
    (type/field-meta ExampleStruct "bar") => (contains {:name "bar"})
    (type/field-meta ExampleStruct "baz") => (contains {:name "baz"})
    (type/field-meta ExampleStruct "qux") => (contains {:name "qux"}))

  (fact "returns nil for missing fields"
    (type/field-meta Identity "missing") => nil?))

(facts "field-meta-list"
  (fact "returns a map of field id's and names for a structure or union"
    (type/field-meta-list Identity) => (contains {:id 1 :name "name" })
    (type/field-meta-list Identity) => (contains {:id 2 :name  "ssn"})
    (type/field-meta-list ExampleStruct) => (contains {:id 3 :name "foo" } )
    (type/field-meta-list ExampleStruct) => (contains {:id 4 :name  "bar"} )
    (type/field-meta-list ExampleStruct) => (contains {:id 5 :name "baz"} )
    (type/field-meta-list ExampleStruct) => (contains {:id 7 :name "qux"})))
