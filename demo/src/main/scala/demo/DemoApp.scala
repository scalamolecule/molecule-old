package demo

import molecule._
import demo.dsl.demo._
import demo.schema.DemoSchema

object DemoApp extends App with DatomicFacade {

  // Make db
  implicit val conn = load(DemoSchema.tx)

  // Load data
  val companyId = Person.name("John").age(26).gender("male").add.id

  // Retrieve data
  val (person, age, gender) = Person.name.age.gender.get.head

  // Verify
  assert((person,age,gender) == ("John", 26, "male"))
  println(s"$person is $age years old and $gender") // John is 26 years old and male
}
