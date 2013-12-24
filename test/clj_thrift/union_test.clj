(ns clj-thrift.union-test
  (:require [clj-thrift.union :as union])
  (:import (clj_thrift.fakes Name Identity ExampleStruct ExampleUnion))
  (:use midje.sweet))


(facts "current-field-id"
  (tabular "returns the currently set field's id"
    (union/current-field-id ?union) => ?id

    ?union                                  ?id
    (Identity/name (Name. "John" "Doe"))    1
    (Identity/ssn "555-55-5555")            2
    (Identity.)                             nil))

(tabular "current-value"
  (fact "returns the currently set field's value"
    (union/current-value ?union) => ?value)

  ?union                                ?value
  (Identity/name (Name. "John" "Doe"))  (Name. "John" "Doe")
  (Identity/ssn "555-55-5555")          "555-55-5555"
  (Identity.)                           nil)

(facts "current-field-name"
  (tabular "returns the currently set field's name"
    (union/current-field-name ?union) => ?name

    ?union                                  ?name
    (Identity/name (Name. "John" "Doe"))    "name"
    (Identity/ssn "555-55-5555")            "ssn"
    (Identity.)                             nil))
