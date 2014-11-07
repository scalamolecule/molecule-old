//package molecule
//package examples.dayOfDatomic.tutorial
//import molecule.examples.dayOfDatomic.schema._
//import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//import datomic.Peer
//import datomic.Util.list
//import molecule.examples.dayOfDatomic.dsl.productsOrder._
//import molecule.examples.dayOfDatomic.dsl.socialNews._
//import scala.collection.JavaConversions._
//import scala.language.existentials
//
//class ProductsAndOrders extends DayOfAtomicSpec {
//
//
//  "ProductsAndOrders (nested data)" >> {
//
//    // See: http://blog.datomic.com/2013/06/component-entities.html
//
//    // Make db
//    implicit val conn = load(ProductsOrderSchema.tx, "Orders")
//
//    // Insert 2 products
//    val List(chocolateId, whiskyId) = Product.description insert("Expensive Chocolate", "Cheap Whisky") ids
//
//
//    // Insert nested data .................................
//
//    // Template for Order with multiple LineItems
//    val order = m(Order.LineItems.apply(LineItem.product.price.quantity))
//
//
//    // Make order with two line items and return created entity id
//    val orderId = order.insert(List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))).ids.last
//
//    // Find id of order with chocolate
//    Order.e.LineItems.Product.description_("Expensive Chocolate").get.head === orderId
//
//    Order.LineItems.apply(LineItem.product.price.quantity)._model === 8
//order.get === 7
//
//    // Touch entity ................................
//
//    // Get all attributes/values of this entity. Sub-component values are recursively retrieved and sorted by their ids
//    orderId.touch === Map(
//      ":db/id" -> 17592186045423L,
//      ":order/lineItems" -> List(
//        Map(
//          ":db/id" -> 17592186045421L,
//          ":lineItem/quantity" -> 1,
//          ":lineItem/price" -> 48.0,
//          ":lineItem/product" -> Map(":db/id" -> chocolateId, ":product/description" -> "Expensive Chocolate")),
//        Map(
//          ":db/id" -> 17592186045422L,
//          ":lineItem/quantity" -> 2,
//          ":lineItem/price" -> 38.0,
//          ":lineItem/product" -> Map(":db/id" -> whiskyId, ":product/description" -> "Cheap Whisky"))
//      ))
//
//    // Retract nested data ............................
//
//    // Retract entity - all subcomponents/lineItems are retracted
//    orderId.retract
//
//    // The products are still there
//    Product.description("Expensive Chocolate" or "Cheap Whisky").ids === List(chocolateId, whiskyId)
//  }
//
//
//}
//
//
////    // Order with multiple LineItems
////    val order = m(Order.orderid.lineItems(m(LineItem.product.price.quantity)))
////
////    val order = m(Order.orderid.lineItems(LineItem.product.price.quantity))
////    val order4 = m(Order.orderid.lineItems(LineItem.product.price.quantity.Product.description_))
////    val order5 = m(Order.orderid.lineItems(LineItem.product.price.Product.description))
////    val order6 = m(Order.orderid.lineItems(Product.description.description.description))
////
////    val order = m(Order.orderid.lineItemsNested(LineItem.product.price.quantity))
////
//////    val order = m(Order.orderid.lineItemsNested(LineItem.product.price.Product.description))
////
////    val order2 = m(Order.orderid ~~ LineItem.product.price.quantity)
////
////    val order20 = m(Order.orderid ~~ LineItem.product.price.Product.description)
////
////
//////    val order21 = m(Order.orderid ~~~ LineItem.product.price.quantity)
////
////    // Same...
////    val order22 = m(Order.orderid ~ LineItem.product)
////    val order23 = m(Order.orderid.LineItems.product)
////
////    val order2a = m(Order.orderid ~ LineItem.product.price)
//////    val order3 = m(Order.orderid ~ LineItem.product.price.Product.description)
////
////
//////    val order = m(Order.orderid.lineItemsNested(Product.description_._LineItem.price.quantity))
////
////
////
////
////    // Make order with two line items and return created entity id
////    //    val orderId = order.insert(List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))).ids
////    //    val orderId = order.insert(List((7, chocolateId, 48.00, 1), (7, whiskyId, 38.00, 2))).ids
////
//////    val orderId = order insert List((chocolateId, 48.00, 1)) //.ids
//////    val orderId = order.insert(List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))).ids
////    val orderId = order.insert(7, List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))).ids
////    val orderId = order4.insert(7, List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))).ids
////    val orderId = order5.insert(7, List((chocolateId, 48.00, ""), (whiskyId, 38.00, ""))).ids
////    val orderId = order20.insert(7, List((chocolateId, 48.00, ""), (whiskyId, 38.00, ""))).ids
////    val orderId = order6.insert(7, List(("","", ""), ("","", ""))).ids
////
//////    val orderId = order21.insert("", chocolateId, 48.00, 1).ids
//////    val orderId = order21.insert(7, chocolateId, 48.00, 1).ids
////
////    val orderId = order22.insert(7, chocolateId).ids
////    val orderId = order23.insert(7, chocolateId).ids
////
////    val orderId = order2.insert(7, List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))).ids
////    val orderId = order2.insert(List((7, List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))))).ids
////
////
////    //    val orderId = order.insert(7).ids
////    //    val orderId = order insert List((1L, chocolateId, 48.00, 1), (1L, whiskyId, 38.00, 2)) last
////    //    val orderId = order insert List((1L, chocolateId, 48.00, 1), (1L, whiskyId, 38.00, 2)) last
////
////    //    val orderId1 = order1.insert List (
////    //      (chocolateId, 48.00, 1),
////    //      (whiskyId, 38.00, 2)
////    //      ) last
////    //      Order.id("id1").LineItems.product.price.quantity.insert
////    //
////    //    Order.LineItems.product.price.quantity.insert
////
////    // Find id of order with chocolate
//////    val orderIdFound = Order.e.lineItems(LineItem.price.Product.description("Expensive Chocolate")).get.head
//////    val orderIdFound = Order.e.lineItems(LineItem.Product.description("Expensive Chocolate")).get.head
////    val orderIdFound = Order.e.LineItems.Product.description("Expensive Chocolate").get.head
////    orderIdFound === orderId