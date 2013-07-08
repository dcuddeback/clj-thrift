# clj-thrift

[![Build Status](https://travis-ci.org/dcuddeback/clj-thrift.png?branch=master)](https://travis-ci.org/dcuddeback/clj-thrift)

A Clojure library for working with [Thrift](http://thrift.apache.org/) types.

## Usage

Add `clj-thrift` to your project's dependencies. If you're using Leiningen, your `project.clj` should look something like this:

~~~clojure
(defproject ...
  :dependencies [[clj-thrift VERSION]])
~~~

Where `VERSION` is the latest version on [Clojars](https://clojars.org/clj-thrift).


### Working With Thrift Values

The examples in this README are for the following Thrift IDL:

~~~thrift
struct Name {
  1: required string firstName;
  2: required string lastName;
}

union Identity {
  1: Name name;
  2: string ssn;
}
~~~

#### Structs

A Thrift object can be constructed recursively from a Clojure map with `clj-thrift.base.build`:

~~~clojure
(require '[clj-thrift.base :as thrift])

(thrift/build Name {:firstName "John" :lastName "Smith"})
; => #<Name Name(firstName:John, lastName:Smith)>

; Also works for unions:
(thrift/build Identity {:name {:firstName "John" :lastName "Smith"}})
; => #<Identity <Identity name:Name(firstName:John, lastName:Smith)>>
~~~

Reading and modifying a object's fields is supported by `clj-thrift.base/value` and
`clj-thrift.base/value!`. `value!` returns the object being modified so that it can be threaded with
`->`.

~~~clojure
(def full-name (-> (Name.)
                 (thrift/value! :firstName "John")
                 (thrift/value! :lastName "Smith")))

(thrift/value full-name :firstName)
; => "John"
~~~

#### Unions

Unions are like structs that can only have one field set at a time (called the current field).
`value!` sets the union's current field. In addition, the current field's value can be queried with
`clj-thrift.union/current-value` and it's tag number with `clj-thrift.union/current-field-id`:

~~~clojure
(require '[clj-thrift.union :as union])

(def id-1 (thrift/build Identity {:name {:firstName "John" :lastName "Smith"}}))
(def id-2 (-> (Identity.)
            (thrift/value! :ssn "555-55-5555")))

(thrift/current-value id-1)
; => #<Name Name(firstName:John, lastName:Smith)>

(thrift/current-value id-2)
; => "555-55-5555"
~~~

### Serialization

`clj-thrift` provides referentially-transparent functions for serializing and deserializing Thrift
objects using Thrift's native serialization classes, `TSerializer` and `TDeserializer`. `clj-thrift`
hides the mutability that is inherent to `TDeserializer` so that serialization and deserialization
behave like pure functions:

~~~clojure
(require '[clj-thrift.serialization :as s])

(def buf (s/serialize (Name. "John" "Smith")))

(s/deserialize Name buf)
; => #<Name Name(firstName:John, lastName:Smith)>
~~~

_Hint:_ `serialize` and `deserialize` can be partially applied to create type- and protocol-specific
serialization functions:

~~~clojure
(require '[clj-thrift.protocol.factory :as protocol])

(def serialize (partial s/serializer (s/serializer (protocol/compact))))
(def deserialize (partial s/deserialize (s/deserializer (protocol/compact)) Name))

(def buf (serialize (Name. "John" "Smith")))

(deserialize buf)
; => #<Name Name(firstName:John, lastName:Smith)>
~~~

Currying the serializer and deserializer can reduce load on the garbage collector.

### Meta Programming

The [`clj-thrift.type`](src/clojure/clj_thrift/type.clj) namespace provides some functions for
looking up meta-data about a Thrift type. This namespace answers questions that can be answered by
inspecting a Thrift class's `metaDataMap` field or `_Fields` enum.

## License

Copyright © 2013 David Cuddeback

Distributed under the [MIT License](LICENSE).
