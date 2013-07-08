(ns clj-thrift.base-test
  (:require [clj-thrift.base :as base])
  (:import (clj_thrift.fakes Person Name Identity ExampleStruct ExampleUnion Containers)
           (java.nio ByteBuffer))
  (:use midje.sweet))


(facts "value"
  (tabular "returns a struct field's value"
    (fact (base/value ?object ?field) => ?value)

    ?object                       ?field      ?value
    (Name. "John" "Doe")          :firstName  "John"
    (Name. "John" "Doe")          :lastName   "Doe"
    (Name. "Jane" "Smith")        :firstName  "Jane"
    (Name. "Jane" "Smith")        :lastName   "Smith"
    (Name.)                       :firstName  nil)

  (tabular "returns a union field's value"
    (fact (base/value ?object ?field) => ?value)

    ?object                       ?field      ?value
    (Identity/ssn "555-55-5555")  :ssn        "555-55-5555"))



(facts "build"

  (tabular "builds structs from maps"
    (fact (base/build ?type ?attributes) => ?result)

    ?type         ?attributes             ?result
    Name          {:firstName "John"
                   :lastName "Doe"}       (Name. "John" "Doe")
    Name          {:firstName "Jane"
                   :lastName "Smith"}     (Name. "Jane" "Smith")
    ExampleStruct {:foo "hello"
                   :bar (Integer. 42)
                   :baz 10.0}             (ExampleStruct. "hello" 42 10.0 nil))


  (tabular "builds empty structs"
    (fact (base/build ?type ?attributes) => ?result)

    ?type         ?attributes             ?result
    ExampleStruct {}                      (ExampleStruct.)
    ExampleStruct nil                     (ExampleStruct.))


  (tabular "builds structs from existing structs"
    (fact (base/build ?type ?object) => ?object)

    ?type         ?object
    Name          (Name. "John" "Doe")
    ExampleStruct (ExampleStruct. "hello" 42 10.0 nil)
    ExampleStruct (ExampleStruct.))


  (tabular "builds unions from maps"
    (fact (base/build ?type ?attributes) => ?result)

    ?type         ?attributes             ?result
    Identity      {:ssn "555-55-5555"}    (Identity/ssn "555-55-5555")
    ExampleUnion  {:foo "hello"}          (ExampleUnion/foo "hello")
    ExampleUnion  {:bar (Integer. 42)}    (ExampleUnion/bar 42)
    ExampleUnion  {:baz 10.0}             (ExampleUnion/baz 10.0))


  (tabular "builds empty unions"
    (fact (base/build ?type ?attributes) => #(not (.isSet %)))

    ?type         ?attributes
    ExampleUnion  {}
    ExampleUnion  nil)


  (tabular "builds unions from existing unions"
    (fact (base/build ?type ?object) => ?object)

    ?type         ?object
    Identity      (Identity/ssn "555-55-5555")
    ExampleUnion  (ExampleUnion/foo "hello"))


  (tabular "builds structs with nested unions"
    (fact (base/build ?type ?attributes) => ?result)

    ?type         ?attributes             ?result
    Person        {:identity
                   {:ssn "555-55-5555"}}  (Person. (Identity/ssn "555-55-5555")))


  (tabular "builds unions with nested structs"
    (fact (base/build ?type ?attributes) => ?result)

    ?type         ?attributes             ?result
    Identity      {:name
                   {:firstName "John"
                    :lastName "Doe"}}     (Identity/name (Name. "John" "Doe")))


  (tabular "compatible with container types"
    (fact (base/build ?type ?attributes) => ?result)

    ?type         ?attributes                         ?result
    Containers    {:aList (range 10)}                 (Containers/aList (range 10))
    Containers    {:aSet (set (range 10))}            (Containers/aSet (set (range 10)))
    Containers    {:aMap
                   (into {} (map (juxt identity str)
                                 (range 10)))}        (Containers/aMap
                                                        (into {} (map (juxt identity str)
                                                                      (range 10))))
    Containers    {:aBinary
                   (ByteBuffer/wrap
                     (byte-array
                       (map byte (range 10))))}       (Containers/aBinary
                                                        (byte-array (map byte (range 10)))))

  (fact "accepts byte array for binary"
    (base/build Containers
                  {:aBinary
                   (byte-array
                     (map byte (range 10)))}) => (Containers/aBinary
                                                   (byte-array
                                                     (map byte (range 10))))))
