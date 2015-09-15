package molecule.examples.dayOfDatomic
import molecule._
import molecule.examples.dayOfDatomic.dsl.productsOrder._
import molecule.examples.dayOfDatomic.schema._
import molecule.util.MoleculeSpec

class ProductsAndOrders extends MoleculeSpec {


  "Nested data, 1 level" >> {

    // See: http://blog.datomic.com/2013/06/component-entities.html

    // Make db
    implicit val conn = load(ProductsOrderSchema, "Orders")

    // Insert 2 products
    val List(chocolateId, whiskyId) = Product.description.insert("Expensive Chocolate", "Cheap Whisky").eids


    // Insert nested data .................................

    // Template for Order with multiple LineItems
    //    val order0 = m(Order.orderid * LineItem.product.price.quantity)
    val order = m(Order.orderid.LineItems * LineItem.product.price.quantity)

    // Make order with two line items and return created entity id
    val orderId = order.insert(23, List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))).eid

    // Find id of order with chocolate
    Order.e.LineItems.Product.description_("Expensive Chocolate").get.head === orderId


    //    def LineItems  : ManyRef[Order, LineItem] with LineItem_1[A] with Group1[LineItem_1, LineItem_2, A] = ???
    // Touch entity ................................

    // Get all attributes/values of this entity. Sub-component values are recursively retrieved
    orderId.touch === Map(
      ":db/id" -> 17592186045422L,
      ":order/lineItems" -> List(
        Map(
          ":db/id" -> 17592186045424L,
          ":lineItem/price" -> 38.0,
          ":lineItem/product" -> Map(
            ":db/id" -> 17592186045420L,
            ":product/description" -> "Cheap Whisky"),
          ":lineItem/quantity" -> 2),
        Map(
          ":db/id" -> 17592186045423L,
          ":lineItem/price" -> 48.0,
          ":lineItem/product" -> Map(
            ":db/id" -> 17592186045419L,
            ":product/description" -> "Expensive Chocolate"),
          ":lineItem/quantity" -> 1)),
      ":order/orderid" -> 23)


    // Retract nested data ............................

    // Retract entity - all subcomponents/lineItems are retracted
    orderId.retract

    // The products are still there
    Product.e.description_("Expensive Chocolate" or "Cheap Whisky").get === List(chocolateId, whiskyId)
  }

  "Nested Data, 2 levels" >> {

    implicit val conn = load(ProductsOrderSchema, "Orders2")

    // Insert 2 products
    val List(chocolateId, whiskyId) = Product.description.insert("Expensive Chocolate", "Cheap Whisky").eids

    // Insert nested data
    val orderId = Order.orderid.LineItems * (LineItem.product.price.quantity.Comments * Comment.text) insert List(
      (23, List(
        (chocolateId, 48.00, 1, List("first", "product")),
        (whiskyId, 38.00, 2, List("second", "is", "best"))
      ))
    ) eid

    // Order lines with correct products added
    Order(23).LineItems.product.get === List(chocolateId, whiskyId)

    // Order line comments are correct
    LineItem.product_(chocolateId).Comments.text.get === List("first", "product")

    // 2 levels of nested data entered
    orderId.touch === Map(
      ":db/id" -> 17592186045422L,
      ":order/lineItems" -> List(
        Map(":lineItem/comments" -> List(
          Map(":db/id" -> 17592186045427L, ":comment/text" -> "second"),
          Map(":db/id" -> 17592186045429L, ":comment/text" -> "best"),
          Map(":db/id" -> 17592186045428L, ":comment/text" -> "is")),
          ":lineItem/price" -> 38.0,
          ":lineItem/quantity" -> 2,
          ":lineItem/product" -> Map(":db/id" -> 17592186045420L, ":product/description" -> "Cheap Whisky"),
          ":db/id" -> 17592186045426L),
        Map(":lineItem/comments" -> List(
          Map(":db/id" -> 17592186045425L, ":comment/text" -> "product"),
          Map(":db/id" -> 17592186045424L, ":comment/text" -> "first")),
          ":lineItem/price" -> 48.0,
          ":lineItem/quantity" -> 1,
          ":lineItem/product" -> Map(":db/id" -> 17592186045419L, ":product/description" -> "Expensive Chocolate"),
          ":db/id" -> 17592186045423L)),
      ":order/orderid" -> 23)
  }


  "Nested Data, 2 levels, multiple attrs" >> {

    implicit val conn = load(ProductsOrderSchema, "Orders3")

    // Insert 2 products
    val List(chocolateId, whiskyId) = Product.description.insert("Expensive Chocolate", "Cheap Whisky").eids

    // Insert nested data with multiple 2nd level args
    val orderId = Order.orderid.LineItems * (LineItem.product.price.quantity.Comments * Comment.text.descr) insert List(
      (23, List(
        (chocolateId, 48.00, 1, List(
          ("first", "1a"),
          ("product", "1b"))),
        (whiskyId, 38.00, 2, List(
          ("second", "2b"),
          ("is", "2b"),
          ("best", "2c")))
      ))
    ) eid

    // 2 levels of nested data entered
    orderId.touch === Map(
      ":db/id" -> 17592186045422L,
      ":order/lineItems" -> List(
        Map(":lineItem/comments" -> List(
          Map(":db/id" -> 17592186045428L, ":comment/descr" -> "2b", ":comment/text" -> "is"),
          Map(":db/id" -> 17592186045427L, ":comment/descr" -> "2b", ":comment/text" -> "second"),
          Map(":db/id" -> 17592186045429L, ":comment/descr" -> "2c", ":comment/text" -> "best")),
          ":lineItem/price" -> 38.0,
          ":lineItem/quantity" -> 2,
          ":lineItem/product" ->
            Map(":db/id" -> 17592186045420L, ":product/description" -> "Cheap Whisky"), ":db/id" -> 17592186045426L),
        Map(":lineItem/comments" -> List(
          Map(":db/id" -> 17592186045425L, ":comment/descr" -> "1b", ":comment/text" -> "product"),
          Map(":db/id" -> 17592186045424L, ":comment/descr" -> "1a", ":comment/text" -> "first")),
          ":lineItem/price" -> 48.0,
          ":lineItem/quantity" -> 1,
          ":lineItem/product" ->
            Map(":db/id" -> 17592186045419L, ":product/description" -> "Expensive Chocolate"), ":db/id" -> 17592186045423L)),
      ":order/orderid" -> 23)
  }


  "Nested Data, 3 levels" >> {

    implicit val conn = load(ProductsOrderSchema, "Orders4")

    // Insert 2 products
    val List(chocolateId, whiskyId) = Product.description.insert("Expensive Chocolate", "Cheap Whisky").eids

    // Insert nested data in 3 levels
    val orderId = Order.orderid.LineItems * (LineItem.product.price.quantity.Comments * (Comment.text.descr.Authors * Person.name)) insert List(
      (23, List(
        (chocolateId, 48.00, 1, List(
          ("first", "1a", List("Marc Grue")),
          ("product", "1b", List("Marc Grue")))),
        (whiskyId, 38.00, 2, List(
          ("second", "2b", List("Don Juan", "Stuart Halloway")),
          ("is", "2b", List("Nick Smith")),
          ("best", "2c", List("test"))))
      ))
    ) eid

    // 3 levels of nested data entered
    orderId.touch === Map(
      ":db/id" -> 17592186045422L,
      ":order/lineItems" -> List(
        Map(":lineItem/comments" -> List(
          Map(":db/id" -> 17592186045434L, ":comment/authors" -> List(
            Map(":db/id" -> 17592186045435L, ":person/name" -> "test")),
            ":comment/descr" -> "2c",
            ":comment/text" -> "best"),
          Map(":db/id" -> 17592186045429L, ":comment/authors" -> List(
            Map(":db/id" -> 17592186045431L, ":person/name" -> "Stuart Halloway"),
            Map(":db/id" -> 17592186045430L, ":person/name" -> "Don Juan")),
            ":comment/descr" -> "2b",
            ":comment/text" -> "second"),
          Map(":db/id" -> 17592186045432L, ":comment/authors" -> List(
            Map(":db/id" -> 17592186045433L, ":person/name" -> "Nick Smith")),
            ":comment/descr" -> "2b",
            ":comment/text" -> "is")),
          ":lineItem/price" -> 38.0,
          ":lineItem/quantity" -> 2,
          ":lineItem/product" -> Map(":db/id" -> 17592186045420L, ":product/description" -> "Cheap Whisky"),
          ":db/id" -> 17592186045428L),
        Map(":lineItem/comments" -> List(
          Map(":db/id" -> 17592186045424L, ":comment/authors" -> List(
            Map(":db/id" -> 17592186045425L, ":person/name" -> "Marc Grue")),
            ":comment/descr" -> "1a",
            ":comment/text" -> "first"),
          Map(":db/id" -> 17592186045426L, ":comment/authors" -> List(
            Map(":db/id" -> 17592186045427L, ":person/name" -> "Marc Grue")),
            ":comment/descr" -> "1b",
            ":comment/text" -> "product")),
          ":lineItem/price" -> 48.0,
          ":lineItem/quantity" -> 1,
          ":lineItem/product" -> Map(":db/id" -> 17592186045419L, ":product/description" -> "Expensive Chocolate"),
          ":db/id" -> 17592186045423L)),
      ":order/orderid" -> 23)
  }


  "Nested Data, empty data sets" >> {

    implicit val conn = load(ProductsOrderSchema, "Orders5")

    // Insert 2 products
    val whiskyId = Product.description.insert("Cheap Whisky").eid

    // Insert nested data in 3 levels - passing empty lists of nested date prevents it to be saved
    val orderId = Order.orderid.LineItems * (LineItem.product.price.quantity.Comments * (Comment.text.descr.Authors * Person.name)) insert List(
      (23, List(
        (whiskyId, 38.00, 2, List(
          ("second", "2b", List("Don Juan")),
          ("is", "2b", Nil),
          ("best", "2c", List())))
      ))
    ) eid

    // 3 levels of nested data entered, some with missing values that are then not asserted
    orderId.touch === Map(
      ":db/id" -> 17592186045421L,
      ":order/lineItems" -> List(
        Map(":lineItem/comments" -> List(
          Map(":db/id" -> 17592186045426L, ":comment/descr" -> "2c", ":comment/text" -> "best"),
          Map(":db/id" -> 17592186045425L, ":comment/descr" -> "2b", ":comment/text" -> "is"),
          Map(":db/id" -> 17592186045423L, ":comment/authors" -> List(
            Map(":db/id" -> 17592186045424L, ":person/name" -> "Don Juan")), ":comment/descr" -> "2b", ":comment/text" -> "second")),
          ":lineItem/price" -> 38.0,
          ":lineItem/quantity" -> 2,
          ":lineItem/product" -> Map(":db/id" -> 17592186045419L, ":product/description" -> "Cheap Whisky"),
          ":db/id" -> 17592186045422L)),
      ":order/orderid" -> 23)
  }


  "Nested Data, null values" >> {

    implicit val conn = load(ProductsOrderSchema, "Orders6")

    // Insert product
    val whiskyId = Product.description.insert("Cheap Whisky").eid

    // We can use `null` for missing cardinality-one values (but not for cardinality-many values)
    val orderId = Order.orderid.LineItems * (LineItem.product.price.quantity.Comments * (Comment.text.descr.Authors * Person.name)) insert List(
      (23, List(
        (whiskyId, 38.00, 2, List(
          ("second", null, List("Don Juan")),
          ("chance", "foo", List("Marc"))
          // ("chance", "foo", null) // Would throw runtime error
        ))
      ))
    ) eid

    // 3 levels of nested data entered, some with missing values that are then not asserted
    orderId.touch === Map(
      ":db/id" -> 17592186045421L,
      ":order/lineItems" -> List(
        Map(":lineItem/comments" -> List(
          Map(":db/id" -> 17592186045425L, ":comment/authors" -> List(
            Map(":db/id" -> 17592186045426L, ":person/name" -> "Marc")), ":comment/descr" -> "foo", ":comment/text" -> "chance"),
          Map(":db/id" -> 17592186045423L, ":comment/authors" -> List(
            Map(":db/id" -> 17592186045424L, ":person/name" -> "Don Juan")), ":comment/text" -> "second")),
          ":lineItem/price" -> 38.0,
          ":lineItem/quantity" -> 2,
          ":lineItem/product" -> Map(":db/id" -> 17592186045419L, ":product/description" -> "Cheap Whisky"),
          ":db/id" -> 17592186045422L)),
      ":order/orderid" -> 23)
  }
}
