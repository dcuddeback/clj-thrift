(ns clj-thrift.serialization-test
  (:require [clj-thrift.serialization :as t]
            [clj-thrift.protocol.factory :as p])
  (:import (org.apache.thrift TSerializer TDeserializer)
           (org.apache.thrift.protocol TCompactProtocol$Factory TBinaryProtocol$Factory
                                       TJSONProtocol$Factory TTupleProtocol$Factory))
  (:use midje.sweet))


(facts "serialization"
  (tabular
    (fact "should be symmetric"
      (->> ?object
        (t/serialize)
        (t/deserialize (type ?object))) => ?object)

    ?object
    ; TStruct
    (clj_thrift.fakes.Name. "John" "Doe")

    ; TUnion
    (clj_thrift.fakes.Identity/name (clj_thrift.fakes.Name. "John" "Doe"))
    (clj_thrift.fakes.Identity/ssn "555-55-5555"))

  (tabular
    (fact "accepts pre-initialized serializer/deserializer"
      (->> ?object
        (t/serialize (TSerializer. ?p-factory))
        (t/deserialize (TDeserializer. ?p-factory) (type ?object))) => ?object)

    ?object                               ?p-factory
    (clj_thrift.fakes.Name. "John" "Doe") (p/binary)
    (clj_thrift.fakes.Name. "John" "Doe") (p/compact)
    (clj_thrift.fakes.Name. "John" "Doe") (p/json)
    (clj_thrift.fakes.Name. "John" "Doe") (p/tuple)))
