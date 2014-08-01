//package demo
//
//import demo.dsl.demo._
//import demo.schema.DemoSchema
//import molecule._
//
//object DemoApp extends App with DatomicFacade {
//
//  // Make db
//  implicit val conn = load(DemoSchema.tx)
//
//  // Load data
//  val companyId = Person.insert.name("John").age(26).gender("male").save.head
//
//  // Retrieve data
//  val (person, age, gender) = Person.name.age.gender.tpls.head
//
//  // Verify
//  assert((person,age,gender) == ("John", 26, "male"))
//  println(s"$person is $age years old and $gender") // John is 26 years old and male
//}
