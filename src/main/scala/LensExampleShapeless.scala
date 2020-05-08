

object LensExampleShapeless extends App {
  import shapeless._
  import test._

  // A pair of ordinary case classes ...
  case class Address(street : String, city : String, postcode : String)
  case class Person(name : String, age : Int, address : Address)

  // Some lenses over Person/Address ...
  val nameLens     = lens[Person].name
  val ageLens      = lens[Person].age
  val addressLens  = lens[Person].address
  val streetLens   = lens[Person].address.street
  val cityLens     = lens[Person].address.city
  val postcodeLens = lens[Person].address.postcode

  // Starting value
  val person = Person("Joe Grey", 37, Address("Southover Street", "Brighton", "BN2 9UA"))

  // Atomic lenses ...

  // Read a field
  val age1 = ageLens.get(person) // Type inferred is Int
  typed[Int](age1)
  assert(age1 == 37)

  // Update a field
  val person2 = ageLens.set(person)(38)
  assert(person2.age == 38)

  // Transform a field
  val person3 = ageLens.modify(person2)(_ + 1)
  assert(person3.age == 39)

  // Read a nested field
  val street = streetLens.get(person3)
  assert(street == "Southover Street")

  // Update a nested field
  val person4 = streetLens.set(person3)("Montpelier Road")
  assert(person4.address.street == "Montpelier Road")

  // Cumulative result of above updates
  assert(person4 == Person("Joe Grey", 39, Address("Montpelier Road", "Brighton", "BN2 9UA")))
  println(person4)

  // Product/composite lenses ...

  // Create a product lens spanning Person and Address
  val nameAgeCityLens = nameLens ~ ageLens ~ cityLens

  val nac1 = nameAgeCityLens.get(person) // Inferred type is the expected tuple type
  typed[(String, Int, String)](nac1)
  assert(nac1 == ("Joe Grey", 37, "Brighton"))
  println(nac1)

  // Update with a tuple distributing values across Person and Address
  val person5 = nameAgeCityLens.set(person)("Joe Soap", 27, "London")
  assert(person5 == Person("Joe Soap", 27, Address("Southover Street", "London", "BN2 9UA")))
  println(person5)
}