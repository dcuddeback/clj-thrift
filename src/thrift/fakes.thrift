namespace * clj_thrift.fakes

struct Name {
  1: required string firstName;
  2: required string lastName;
}

union Identity {
  1: Name name;
  2: string ssn;
}

struct ExampleStruct {
  3: string foo;
  4: i32 bar;
  5: double baz
  7: list<byte> qux;
}

union ExampleUnion {
  3: string foo;
  5: i32 bar;
  6: double baz;
}
