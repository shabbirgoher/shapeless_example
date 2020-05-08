
import io.circe.Json
import io.circe.generic.auto._
import io.circe.optics.JsonPath._
import io.circe.parser._
import monocle.{Optional, Traversal}
import shapeless._
import shapeless.ops.hlist.RightFolder
import shapeless.syntax.std.tuple._

case class Extra(extraKey: String)

case class Fruit(id: Int, description: String)

object Main extends App {
  val json: Json = parse(
    """|{
       |  "order": {
       |    "customer": {
       |      "name": "Custy McCustomer",
       |      "age": 10,
       |      "contactDetails": {
       |        "address": "1 Fake Street, London, England",
       |        "phone": "0123-456-789"
       |      }
       |    },
       |    "items": [{
       |      "id": 123,
       |      "description": "banana",
       |      "quantity": 1
       |    }, {
       |      "id": 456,
       |      "description": "apple",
       |      "quantity": 2
       |    }],
       |    "total": 123,
       |    "more": [{
       |      "extra": [{
       |        "extraKey": "extra-1"
       |      }, {
       |        "extraKey": "extra-2"
       |      }]
       |    }]
       |  }
       |}""".stripMargin).getOrElse(Json.Null)


  val customerName: Optional[Json, String] = root.order.customer.name.string
  val age: Optional[Json, Int] = root.order.customer.age.int
  val address: Optional[Json, String] = root.order.customer.contactDetails.address.string
  val extra: Traversal[Json, Extra] = root.order.more.each.extra.each.as[Extra]
  val item: Traversal[Json, Fruit] = root.order.items.each.as[Fruit]

  object readJson extends Poly2 {
    implicit def optionVal[A, B <: HList]: Case.Aux[
      Optional[Json, A],
      (B, Json),
      (Option[A] :: B, Json)
    ] =
      at[Optional[Json, A], (B, Json)] {
        case (t, (list, json)) =>
          val value = t.getOption(json)
          (value :: list, json)
      }

    implicit def listVal[A, B <: HList]: Case.Aux[
      monocle.Traversal[Json, A],
      (B, Json),
      (List[A] :: B, Json)
    ] = at[monocle.Traversal[Json, A], (B, Json)] {
      case (col, (values, cursor)) =>
        (col.getAll(json) :: values, cursor)
    }
  }

  private val parsed: Option[Int] :: Option[String] :: List[Extra] :: List[Fruit] :: HNil = (age, customerName, extra, item)
    .foldRight((HNil: HNil, json))(readJson)
    ._1

  println(parsed)
}
