package moleculeTests.tests.examples.datomic.dayOfDatomic

import molecule.datomic.api.out5._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.examples.datomic.dayOfDatomic.dsl.ProductsOrder._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

// See: http://blog.datomic.com/2013/06/component-entities.html

object ProductsAndOrders extends AsyncTestSuite {

  lazy val tests = Tests {

    //  peerOnly = true
    //  devLocalOnly = true

    "Nested data, 1 level without initial namespace asserts" - products { implicit conn =>
      for {
        // Insert 2 products
        tx <- Product.description.insert("Expensive Chocolate", "Cheap Whisky")
        List(chocolateId, whiskyId) = tx.eids

        // Insert nested data .................................

        // We don't necessarily have to assert a fact of the initial namespace
        tx2 <- m(Order.LineItems * LineItem.product.price.quantity) insert
          List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))
        List(order, l1, l2) = tx2.eids

        //    println(order.touchQuoted)
        _ <- order.touch.map(_ ==> Map(
          ":db/id" -> order,
          ":Order/lineItems" -> List(Map(
            ":db/id" -> l1,
            ":LineItem/price" -> 48.0,
            ":LineItem/product" -> Map(
              ":db/id" -> chocolateId,
              ":Product/description" -> "Expensive Chocolate"),
            ":LineItem/quantity" -> 1), Map(
            ":db/id" -> l2,
            ":LineItem/price" -> 38.0,
            ":LineItem/product" -> Map(
              ":db/id" -> whiskyId,
              ":Product/description" -> "Cheap Whisky"),
            ":LineItem/quantity" -> 2))))

        // We can get the created order entity id and its lineItem's data
        _ <- m(Order.e.LineItems * LineItem.product.price.quantity).get.map(_ ==> List(
          (order, List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2)))
        ))

        // Or we can omit the order entity id and get the lineItem data in groups
        // for each Order found (only 1 here)
        _ <- m(Order.LineItems * LineItem.product.price.quantity).get.map(_ ==> List(
          List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))
        ))

        // If we query for only 1 attribute we get a list of values instead of tuples of values
        _ <- m(Order.LineItems * LineItem.product).get.map(_ ==> List(
          List(chocolateId, whiskyId)
        ))
      } yield ()
    }

    "Nested Data, 1 level" - products { implicit conn =>
      for {
        // Insert 2 products
        tx <- Product.description.insert("Expensive Chocolate", "Cheap Whisky")
        List(chocolateId, whiskyId) = tx.eids

        // Template for Order with multiple LineItems
        order = m(Order.orderid.LineItems * LineItem.product.price.quantity)

        // Insert nested data .................................

        // Make order with two line items and return created entity id
        tx2 <- order.insert(23, List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2)))
        List(orderId, l1, l2) = tx2.eids

        // Find id of order with chocolate
        _ <- Order.e.LineItems.Product.description_("Expensive Chocolate").get.map(_.head ==> orderId)


        // Touch entity ................................

        // Get all attributes/values of this entity. Sub-component values are
        // recursively retrieved
        _ <- orderId.touch.map(_ ==> Map(
          ":db/id" -> orderId,
          ":Order/lineItems" -> List(
            Map(":db/id" -> l1, ":LineItem/price" -> 48.0, ":LineItem/product" ->
              Map(":db/id" -> chocolateId, ":Product/description" -> "Expensive Chocolate"), ":LineItem/quantity" -> 1),
            Map(":db/id" -> l2, ":LineItem/price" -> 38.0, ":LineItem/product" ->
              Map(":db/id" -> whiskyId, ":Product/description" -> "Cheap Whisky"), ":LineItem/quantity" -> 2)),
          ":Order/orderid" -> 23))

        // We can re-use the `order` molecule
        _ <- order.get.map(_ ==> List(
          (23, List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2)))
        ))

        // Retract nested data ............................

        // Retract entity - all subcomponents/lineItems are retracted
        _ <- orderId.retract

        // The products are still there
        _ <- Product.e.description_("Expensive Chocolate" or "Cheap Whisky").get.map(_ ==>
          List(chocolateId, whiskyId))
      } yield ()
    }

    "Nested Data, 1 level, with non-asserted values" - products { implicit conn =>
      for {
        // Insert 2 products
        tx <- Product.description.insert("Expensive Chocolate", "Cheap Whisky", "Licorice")
        List(chocolateId, whiskyId, licoriceId) = tx.eids

        // Template for Order with multiple LineItems and optional quantity
        order = m(Order.orderid.LineItems * LineItem.product.price.quantity$)


        // Insert .................................

        // Make order with two line items and return created entity id
        tx2 <- order insert List(
          (23, List(
            (chocolateId, 48.00, Some(1)),
            (whiskyId, 38.00, None),
            (licoriceId, 77.00, Some(2)))),
          (24, List(
            (whiskyId, 38.00, Some(3)),
            (licoriceId, 77.00, Some(4)))))

        List(order23, l1, l2, l3, order24, ll1, ll2) = tx2.eids


        // Find id of orders containing various products
        _ <- Order.e.LineItems.Product.description_("Expensive Chocolate").get.map(_ ==>
          List(order23))

        _ <- Order.e.LineItems.Product.description_("Licorice").get.map(_.sorted ==>
          List(order23, order24))


        // Touch ................................

        // Get all attributes/values of this entity. Sub-component values are recursively retrieved
        _ <- order23.touch.map(_ ==> Map(
          ":db/id" -> order23,
          ":Order/lineItems" -> List(
            Map(":db/id" -> l1, ":LineItem/price" -> 48.0, ":LineItem/product" ->
              Map(":db/id" -> chocolateId, ":Product/description" -> "Expensive Chocolate"), ":LineItem/quantity" -> 1),
            Map(":db/id" -> l2, ":LineItem/price" -> 38.0, ":LineItem/product" ->
              // Whisky _is_ touched although it has no quantity asserted!
              Map(":db/id" -> whiskyId, ":Product/description" -> "Cheap Whisky")),
            Map(":db/id" -> l3, ":LineItem/price" -> 77.0, ":LineItem/product" ->
              Map(":db/id" -> licoriceId, ":Product/description" -> "Licorice"), ":LineItem/quantity" -> 2)),
          ":Order/orderid" -> 23))

        _ <- order24.touch.map(_ ==> Map(
          ":db/id" -> order24,
          ":Order/lineItems" -> List(
            Map(":db/id" -> ll1, ":LineItem/price" -> 38.0, ":LineItem/product" ->
              Map(":db/id" -> whiskyId, ":Product/description" -> "Cheap Whisky"), ":LineItem/quantity" -> 3),
            Map(":db/id" -> ll2, ":LineItem/price" -> 77.0, ":LineItem/product" ->
              Map(":db/id" -> licoriceId, ":Product/description" -> "Licorice"), ":LineItem/quantity" -> 4)),
          ":Order/orderid" -> 24))


        // Get ................................

        // Get adjacent facts
        _ <- Order.orderid.LineItems.quantity.price.Product.e.description
          .get.map(_.sortBy(t => (t._1, t._2)) ==> List(
          // whisky for order 23 is _not_ fetched since it has no quantity asserted!
          (23, 1, 48.0, chocolateId, "Expensive Chocolate"),
          (23, 2, 77.0, licoriceId, "Licorice"),
          (24, 3, 38.0, whiskyId, "Cheap Whisky"),
          (24, 4, 77.0, licoriceId, "Licorice"),
        ))

        // Make `quantity` optional (by appending `$`) and get all facts wether
        // quantity is asserted or not.
        // Quantities are then returned as Option[Int]
        _ <- Order.orderid.LineItems.quantity$.price.Product.e.description
          .get.map(_.sortBy(t => (t._1, t._5)) ==> List(
          (23, None, 38.0, whiskyId, "Cheap Whisky"),
          (23, Some(1), 48.0, chocolateId, "Expensive Chocolate"),
          (23, Some(2), 77.0, licoriceId, "Licorice"),
          (24, Some(3), 38.0, whiskyId, "Cheap Whisky"),
          (24, Some(4), 77.0, licoriceId, "Licorice")
        ))

        // Get nested data
        _ <- m(Order.e.orderid.LineItems * LineItem.quantity.price).get.map(_ ==> List(
          (order23, 23, List((1, 48.0), (2, 77.0))),
          (order24, 24, List((3, 38.0), (4, 77.0)))))

        _ <- m(Order.orderid.LineItems * LineItem.quantity.price).get.map(_ ==> List(
          (23, List((1, 48.0), (2, 77.0))),
          (24, List((3, 38.0), (4, 77.0)))))

        _ <- m(Order.orderid.LineItems * LineItem.quantity).get.map(_ ==> List(
          (23, List(1, 2)),
          (24, List(3, 4))))


        // Retract ............................

        // Retract entity - all subcomponents/lineItems are retracted
        _ <- order23.retract

        // The products are still there
        _ <- Product.e.description_("Expensive Chocolate" or "Cheap Whisky")
          .get.map(_ ==> List(chocolateId, whiskyId))
      } yield ()
    }

    "Mixing nested and adjacent data" - products { implicit conn =>
      for {
        // Insert .................................

        // Although we wouldn't like to create redundant products, we'll make some
        // here anyway just to demonstrate that the nested part of a molecule can
        // contain an adjacent card-one reference
        tx <- m(Order.orderid.LineItems * LineItem.price.quantity$.Product.description) insert List(
          (23, List(
            (48.00, Some(1), "Expensive Chocolate"),
            (38.00, None, "Cheap Whisky"),
            (77.00, Some(2), "Licorice"))),
          (24, List(
            (38.00, Some(3), "Cheap Whisky"),
            (77.00, Some(4), "Licorice"))))
        List(order23, l1, p1, l2, p2, l3, p3, order24, ll1, pp1, ll2, pp2) = tx.eids

        // Find id of orders containing various products
        _ <- Order.e.LineItems.Product.description_("Expensive Chocolate").get.map(_ ==>
          List(order23))

        _ <- Order.e.LineItems.Product.description_("Licorice").get.map(_.sorted ==>
          List(order23, order24))


        // Touch ................................

        // Get all attributes/values of this entity. Sub-component values are
        // recursively retrieved
        _ <- order23.touch.map(_ ==> Map(
          ":db/id" -> order23,
          ":Order/lineItems" -> List(
            Map(":db/id" -> l1, ":LineItem/price" -> 48.0, ":LineItem/product" ->
              Map(":db/id" -> p1, ":Product/description" -> "Expensive Chocolate"), ":LineItem/quantity" -> 1),
            Map(":db/id" -> l2, ":LineItem/price" -> 38.0, ":LineItem/product" ->
              Map(":db/id" -> p2, ":Product/description" -> "Cheap Whisky")),
            Map(":db/id" -> l3, ":LineItem/price" -> 77.0, ":LineItem/product" ->
              Map(":db/id" -> p3, ":Product/description" -> "Licorice"), ":LineItem/quantity" -> 2)),
          ":Order/orderid" -> 23))

        _ <- order24.touch.map(_ ==> Map(
          ":db/id" -> order24,
          ":Order/lineItems" -> List(
            Map(":db/id" -> ll1, ":LineItem/price" -> 38.0, ":LineItem/product" ->
              Map(":db/id" -> pp1, ":Product/description" -> "Cheap Whisky"), ":LineItem/quantity" -> 3),
            Map(":db/id" -> ll2, ":LineItem/price" -> 77.0, ":LineItem/product" ->
              Map(":db/id" -> pp2, ":Product/description" -> "Licorice"), ":LineItem/quantity" -> 4)),
          ":Order/orderid" -> 24))

        // Get ................................

        // Get nested data
        _ <- m(Order.orderid.LineItems * LineItem.price.quantity$.Product.description).get.map(_ ==> List(
          (23, List(
            (48.00, Some(1), "Expensive Chocolate"),
            (38.00, None, "Cheap Whisky"),
            (77.00, Some(2), "Licorice"))),
          (24, List(
            (38.00, Some(3), "Cheap Whisky"),
            (77.00, Some(4), "Licorice")))))
      } yield ()
    }

    "Nested Data, 2 levels" - products { implicit conn =>
      for {
        // Insert 2 products
        tx <- Product.description.insert("Expensive Chocolate", "Cheap Whisky")
        List(chocolateId, whiskyId) = tx.eids

        // Insert nested data
        tx2 <- Order.orderid.LineItems * (
          LineItem.product.price.quantity.Comments * Comment.text) insert List(
          (23, List(
            (chocolateId, 48.00, 1, List("first", "product")),
            (whiskyId, 38.00, 2, List("second", "is", "best"))
          ))
        )
        List(o1, l1, c1, c2, l2, c3, c4, c5) = tx2.eids

        // Order lines with correct products added
        _ <- Order(o1).LineItems.product.get.map(_ ==> List(chocolateId, whiskyId))

        // Order line comments are correct
        _ <- LineItem.product_(chocolateId).Comments.text.get.map(_ ==> List("first", "product"))

        // 2 levels of nested data entered
        _ <- o1.touch.map(_ ==> Map(
          ":db/id" -> o1,
          ":Order/lineItems" -> List(
            Map(":LineItem/comments" -> List(
              Map(":db/id" -> c1, ":Comment/text" -> "first"),
              Map(":db/id" -> c2, ":Comment/text" -> "product")),
              ":LineItem/price" -> 48.0, ":LineItem/quantity" -> 1, ":LineItem/product" ->
                Map(":db/id" -> chocolateId, ":Product/description" -> "Expensive Chocolate"),
              ":db/id" -> l1),
            Map(":LineItem/comments" -> List(
              Map(":db/id" -> c3, ":Comment/text" -> "second"),
              Map(":db/id" -> c4, ":Comment/text" -> "is"),
              Map(":db/id" -> c5, ":Comment/text" -> "best")),
              ":LineItem/price" -> 38.0,
              ":LineItem/quantity" -> 2,
              ":LineItem/product" ->
                Map(":db/id" -> whiskyId, ":Product/description" -> "Cheap Whisky"),
              ":db/id" -> l2)),
          ":Order/orderid" -> 23))


        _ <- m(Order.orderid.LineItems * (
          LineItem.product.price.quantity.Comments * Comment.text)).get.map(_ ==> List(
          (23, List(
            (chocolateId, 48.00, 1, List("first", "product")),
            (whiskyId, 38.00, 2, List("second", "is", "best"))
          ))
        ))
      } yield ()
    }

    "Nested Data, 2 levels, multiple attrs" - products { implicit conn =>
      for {
        // Insert 2 products
        tx <- Product.description.insert("Expensive Chocolate", "Cheap Whisky")
        List(chocolateId, whiskyId) = tx.eids

        // Insert nested data with multiple 2nd level args
        tx2 <- Order.orderid.LineItems * (
          LineItem.product.price.quantity.Comments * Comment.text.descr) insert List(
          (23, List(
            (chocolateId, 48.00, 1, List(
              ("first", "1a"),
              ("product", "1b"))),
            (whiskyId, 38.00, 2, List(
              ("second", "2b"),
              ("is", "2b"),
              ("best", "2c")))
          ))
        )
        List(o1, l1, c1, c2, l2, c3, c4, c5) = tx2.eids

        // 2 levels of nested data entered
        _ <- o1.touch.map(_ ==> Map(
          ":db/id" -> o1,
          ":Order/lineItems" -> List(
            Map(":LineItem/comments" -> List(
              Map(":db/id" -> c1, ":Comment/descr" -> "1a", ":Comment/text" -> "first"),
              Map(":db/id" -> c2, ":Comment/descr" -> "1b", ":Comment/text" -> "product")),
              ":LineItem/price" -> 48.0,
              ":LineItem/quantity" -> 1,
              ":LineItem/product" ->
                Map(":db/id" -> chocolateId, ":Product/description" -> "Expensive Chocolate"),
              ":db/id" -> l1),
            Map(":LineItem/comments" -> List(
              Map(":db/id" -> c3, ":Comment/descr" -> "2b", ":Comment/text" -> "second"),
              Map(":db/id" -> c4, ":Comment/descr" -> "2b", ":Comment/text" -> "is"),
              Map(":db/id" -> c5, ":Comment/descr" -> "2c", ":Comment/text" -> "best")),
              ":LineItem/price" -> 38.0,
              ":LineItem/quantity" -> 2,
              ":LineItem/product" ->
                Map(":db/id" -> whiskyId, ":Product/description" -> "Cheap Whisky"),
              ":db/id" -> l2)),
          ":Order/orderid" -> 23))


        _ <- m(Order.orderid.LineItems * (
          LineItem.quantity.price.Comments * Comment.text.descr)).get.map(_ ==> List(
          (23, List(
            (1, 48.00, List(
              ("first", "1a"),
              ("product", "1b"))),
            (2, 38.00, List(
              ("second", "2b"),
              ("is", "2b"),
              ("best", "2c")))
          ))
        ))
      } yield ()
    }


    "Nested Data, 3 levels" - products { implicit conn =>
      for {
        // Insert 2 products
        tx <- Product.description.insert("Expensive Chocolate", "Cheap Whisky")
        List(chocolateId, whiskyId) = tx.eids

        // Insert nested data in 3 levels
        tx2 <- Order.orderid.LineItems * (
          LineItem.product.price.quantity.Comments * (
            Comment.text.descr.Authors * Person.name)) insert List(
          (23, List(
            (chocolateId, 48.00, 1, List(
              ("first", "1a", List("Marc Grue")),
              ("product", "1b", List("Marc Grue")))),
            (whiskyId, 38.00, 2, List(
              ("second", "2b", List("Don Juan", "Stuart Halloway")),
              ("is", "2b", List("Nick Smith")),
              ("best", "2c", List("test"))))
          ))
        )
        List(o1, l1, c1, a1, c2, a2, l2, c3, a3, a4, c4, a5, c5, a6) = tx2.eids

        /* 3 levels of nested data entered*/
        _ <- o1.touch.map(_ ==> Map(
          ":db/id" -> o1,
          ":Order/lineItems" -> List(
            Map(
              ":LineItem/comments" -> List(
                Map(
                  ":db/id" -> c1, ":Comment/authors" -> List(
                    Map(":db/id" -> a1, ":Person/name" -> "Marc Grue")),
                  ":Comment/descr" -> "1a",
                  ":Comment/text" -> "first"),
                Map(":db/id" -> c2, ":Comment/authors" -> List(
                  Map(":db/id" -> a2, ":Person/name" -> "Marc Grue")),
                  ":Comment/descr" -> "1b",
                  ":Comment/text" -> "product")),
              ":LineItem/price" -> 48.0,
              ":LineItem/quantity" -> 1,
              ":LineItem/product" ->
                Map(":db/id" -> chocolateId, ":Product/description" -> "Expensive Chocolate"),
              ":db/id" -> l1),
            Map(":LineItem/comments" -> List(
              Map(":db/id" -> c3, ":Comment/authors" -> List(
                Map(":db/id" -> a3, ":Person/name" -> "Don Juan"),
                Map(":db/id" -> a4, ":Person/name" -> "Stuart Halloway")),
                ":Comment/descr" -> "2b",
                ":Comment/text" -> "second"),
              Map(":db/id" -> c4, ":Comment/authors" -> List(
                Map(":db/id" -> a5, ":Person/name" -> "Nick Smith")),
                ":Comment/descr" -> "2b",
                ":Comment/text" -> "is"),
              Map(":db/id" -> c5, ":Comment/authors" -> List(
                Map(":db/id" -> a6, ":Person/name" -> "test")),
                ":Comment/descr" -> "2c",
                ":Comment/text" -> "best")),
              ":LineItem/price" -> 38.0,
              ":LineItem/quantity" -> 2,
              ":LineItem/product" ->
                Map(":db/id" -> whiskyId, ":Product/description" -> "Cheap Whisky"),
              ":db/id" -> l2)),
          ":Order/orderid" -> 23))

        _ <- m(Order.orderid.LineItems * (
          LineItem.product.price.quantity.Comments * (
            Comment.text.descr.Authors * Person.name))).get.map(_ ==> List(
          (23, List(
            (chocolateId, 48.00, 1, List(
              ("first", "1a", List("Marc Grue")),
              ("product", "1b", List("Marc Grue")))),
            (whiskyId, 38.00, 2, List(
              ("second", "2b", List("Don Juan", "Stuart Halloway")),
              ("is", "2b", List("Nick Smith")),
              ("best", "2c", List("test"))))
          ))
        ))
      } yield ()
    }

    "Nested Data, empty data sets" - products { implicit conn =>
      for {
        // Insert 2 products
        tx <- Product.description.insert("Cheap Whisky")
        whiskyId = tx.eid

        // Insert nested data in 3 levels - passing empty lists of nested date
        // prevents it to be saved
        tx2 <- Order.orderid.LineItems * (
          LineItem.product.price.quantity.Comments * (
            Comment.text.descr.Authors * Person.name)) insert List(
          (23, List(
            (whiskyId, 38.00, 2, List(
              ("second", "2b", List("Don Juan")),
              ("is", "2b", Nil),
              ("best", "2c", List())))
          ))
        )
        List(o1, l1, c1, a1, c2, c3) = tx2.eids

        // 3 levels of nested data entered, some with missing values that are
        // then not asserted
        _ <- o1.touch.map(_ ==> Map(
          ":db/id" -> o1,
          ":Order/lineItems" -> List(
            Map(":LineItem/comments" -> List(
              Map(":db/id" -> c1, ":Comment/authors" -> List(
                Map(":db/id" -> a1, ":Person/name" -> "Don Juan")),
                ":Comment/descr" -> "2b",
                ":Comment/text" -> "second"),
              Map(":db/id" -> c2, ":Comment/descr" -> "2b", ":Comment/text" -> "is"),
              Map(":db/id" -> c3, ":Comment/descr" -> "2c", ":Comment/text" -> "best")),
              ":LineItem/price" -> 38.0,
              ":LineItem/quantity" -> 2,
              ":LineItem/product" ->
                Map(":db/id" -> whiskyId, ":Product/description" -> "Cheap Whisky"),
              ":db/id" -> l1)),
          ":Order/orderid" -> 23))

        // Comments with no author are not fetched
        _ <- m(Order.orderid.LineItems * (
          LineItem.product.price.quantity.Comments * (
            Comment.text.descr.Authors * Person.name))).get.map(_ ==> List(
          (23, List(
            (whiskyId, 38.00, 2, List(
              ("second", "2b", List("Don Juan"))))
          ))
        ))
      } yield ()
    }

    "Nested Data, non-asserted values" - products { implicit conn =>
      for {
        // Insert product
        tx <- Product.description.insert("Cheap Whisky")
        whiskyId = tx.eid

        // We can use an optional attribute (`Comment.descr$`) for missing values
        tx2 <- Order.orderid.LineItems * (
          LineItem.product.price.quantity.Comments * (
            Comment.text.descr$.Authors * Person.name)) insert List(
          (23, List(
            (whiskyId, 38.00, 2, List(
              ("second", None, List("Don Juan")),
              ("chance", Some("foo"), List("Marc"))
            ))
          ))
        )
        List(o1, l1, c1, a1, c2, a2) = tx2.eids

        // 3 levels of nested data entered, some with missing values that are
        // then not asserted
        _ <- o1.touch.map(_ ==> Map(
          ":db/id" -> o1,
          ":Order/lineItems" -> List(
            Map(":LineItem/comments" -> List(
              Map(":db/id" -> c1, ":Comment/authors" -> List(
                Map(":db/id" -> a1, ":Person/name" -> "Don Juan")),
                ":Comment/text" -> "second"),
              Map(":db/id" -> c2, ":Comment/authors" -> List(
                Map(":db/id" -> a2, ":Person/name" -> "Marc")),
                ":Comment/descr" -> "foo",
                ":Comment/text" -> "chance")),
              ":LineItem/price" -> 38.0,
              ":LineItem/quantity" -> 2,
              ":LineItem/product" ->
                Map(":db/id" -> whiskyId, ":Product/description" -> "Cheap Whisky"),
              ":db/id" -> l1)),
          ":Order/orderid" -> 23))

        // Comment with no `description` is not fetched
        _ <- m(Order.orderid.LineItems * (
          LineItem.product.price.quantity.Comments * (
            Comment.text.descr.Authors * Person.name))).get.map(_ ==> List(
          (23, List(
            (whiskyId, 38.00, 2, List(
              ("chance", "foo", List("Marc"))
            ))
          ))
        ))

        // Comment with optional `description` is fetched
        _ <- m(Order.orderid.LineItems * (
          LineItem.product.price.quantity.Comments * (
            Comment.text.descr$.Authors * Person.name))).get.map(_ ==> List(
          (23, List(
            (whiskyId, 38.00, 2, List(
              ("second", None, List("Don Juan")),
              ("chance", Some("foo"), List("Marc"))
            ))
          ))
        ))
      } yield ()
    }

    "Corner case with flat card-many + nested" - products { implicit conn =>
      for {
        // Insert product
        tx <- Product.description.insert("Cheap Whisky")
        whiskyId = tx.eid

        // We can use an optional attribute (`Comment.descr$`) for missing values
        tx2 <- Order.orderid.LineItems * (
          LineItem.product.price.quantity.text.Comments * (
            Comment.text.descr$.Authors * Person.name)) insert List(
          (23, List(
            (whiskyId, 38.00, 2, "in stock", List(
              ("second", None, List("Don Juan")),
              ("chance", Some("foo"), List("Marc"))
            ))
          ))
        )
        orderId = tx2.eid

        // Combining flat card-many (LineItems.text) + nested
        _ <- Order.orderid_(23).LineItems.text
          .Comments.*(Comment.text
          .Authors.*(Person.name
        )).get.map(_ ==> List(
          ("in stock", List(
            ("second", List("Don Juan")),
            ("chance", List("Marc"))))
        ))
      } yield ()
    }
  }
}
