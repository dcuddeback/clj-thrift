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
