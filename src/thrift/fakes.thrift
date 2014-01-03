namespace * clj_thrift.fakes

struct Name {
  1: required string firstName;
  2: required string lastName;
}

union Identity {
  1: Name name;
  2: string ssn;
}

struct Person {
  1: Identity identity
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

union Containers {
  1: list<i64> aList;
  2: set<i64> aSet;
  3: map<i64,string> aMap;
  4: binary aBinary;
}

union ExampleAll {
  1: Person person
  2: ExampleStruct examplestruct
  3: ExampleUnion exampleunion
}
