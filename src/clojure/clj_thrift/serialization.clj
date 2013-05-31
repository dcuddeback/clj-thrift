(ns clj-thrift.serialization
  (:import [org.apache.thrift TSerializer TDeserializer]))

;; ## Serializers

(defn serializer
  "Returns a Thrift serializer. If provided with a protocol factory, that factory will be used.
  Otherwise, the default protocol factory (binary) will be used."
  ([]
   (TSerializer.))
  ([protocol-factory]
   (TSerializer. protocol-factory)))

(defn deserializer
  "Returns a Thrift deserializer. If provided with a protocol factory, that factory will be used.
  Otherwise, the default protocol factory (binary) will be used."
  ([]
   (TDeserializer.))
  ([protocol-factory]
   (TDeserializer. protocol-factory)))


;; ## Serialization

(defn serialize
  "Serializes a Thrift object. If provided with a serializer, that serializer will be used.
  Otherwise, a default serializer will be constructed. The serializer can be curried into a closure
  to reuse the same instance and reduce stress on the garbage collector.

    (serialize object)
    (serialize (serializer) object)

    ; reuses serializer for each object being serialized
    (def f (partial serialize (serializer)))
    (map f objects)"
  ([object]
   (serialize (serializer) object))
  ([serializer object]
    (.serialize serializer object)))

(defn deserialize
  "Deserializes a Thrift object from a byte array. One must provide the class (uninitialized) of the
  object that is expected to be in the byte array. The return value will be a new instance of the
  class, populated with data from the serialized byte array.

  One can provide a deserializer as the first argument to customize the serialization protocol. If
  it's not provided, the default deserializer will be used. The deserializer can be curried into a
  closure to reuse the same instance and reduce stress on the garbage collector.

    (deserialize Name buffer)
    (deserialize (deserializer) Name buffer)

    ; reuses deserializer for each object being deserialized
    (def f (partial deserialize (deserializer) Name))
    (map f buffers)"
  ([type buffer]
   (deserialize (deserializer) type buffer))
  ([deserializer type buffer]
    (letfn [(deserialize-obj [object buffer]
              (.deserialize deserializer object buffer)
              object)]
      (deserialize-obj (.newInstance type) buffer))))
