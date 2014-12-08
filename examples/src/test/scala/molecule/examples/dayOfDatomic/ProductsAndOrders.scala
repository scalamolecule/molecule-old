package molecule.examples.dayOfDatomic
import molecule._
import molecule.examples.dayOfDatomic.dsl.productsOrder._
import molecule.examples.dayOfDatomic.schema._
import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec

class ProductsAndOrders extends DayOfAtomicSpec {


  "Nested data" >> {

    // See: http://blog.datomic.com/2013/06/component-entities.html

    // Make db
    implicit val conn = load(ProductsOrderSchema.tx, "Orders")

    // Insert 2 products
    val List(chocolateId, whiskyId) = Product.description.insert("Expensive Chocolate", "Cheap Whisky").ids


    // Insert nested data .................................

    // Template for Order with multiple LineItems
    val order = m(Order.LineItems(LineItem.product.price.quantity))
//    val order = m(Order.orderid.LineItems(LineItem.product.price.quantity))

    // Make order with two line items and return created entity id
    val orderId = order.insert(List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))).id
//    val orderId = order.insert(333, List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))).id
    /*
    ## 26: Model2Transaction
    ========================================================================
    1          Model(
      1          Group(
        1          Bond(order,lineItems,LineItem)
        2          Atom(lineItem,product,Long,1,VarValue,None,List())
        3          Atom(lineItem,price,Double,1,VarValue,None,List())
        4          Atom(lineItem,quantity,Int,1,VarValue,None,List())))
    ------------------------------------------------
    2          List(
      1          :db/add       'parentId                          :order/lineItems           List(
        1          :db/add       'v                                 :lineItem/product             'arg
        2          :db/add       'e                                 :lineItem/price               'arg
        3          :db/add       'e                                 :lineItem/quantity            'arg))
    ------------------------------------------------
    3          List(
      1          :db/add       'parentId                          :order/lineItems           List(
        1          :db/add       'v                                 :lineItem/product             'arg
        2          :db/add       'e                                 :lineItem/price               'arg
        3          :db/add       'e                                 :lineItem/quantity            'arg))
    ------------------------------------------------
    4          ArrayBuffer(
    List(
    Add(#db/id[:db.part/user -1000009],:order/lineItems,#db/id[:db.part/user -1000010]),
    Add(#db/id[:db.part/user -1000010],:lineItem/product,17592186045418),
    Add(#db/id[:db.part/user -1000010],:lineItem/price,48.0),
    Add(#db/id[:db.part/user -1000010],:lineItem/quantity,1),
    Add(#db/id[:db.part/user -1000009],:order/lineItems,#db/id[:db.part/user -1000011]),
    Add(#db/id[:db.part/user -1000011],:lineItem/product,17592186045419),
    Add(#db/id[:db.part/user -1000011],:lineItem/price,38.0),
    Add(#db/id[:db.part/user -1000011],:lineItem/quantity,2)))
    ------------------------------------------------
    5          List(
    )
    ------------------------------------------------
    6          List(
      1          List(
    ))
    ------------------------------------------------
    7          ArrayBuffer(List(List((17592186045418,48.0,1), (17592186045419,38.0,2))))
    ========================================================================
*/

    // Find id of order with chocolate
    Order.e.LineItems.Product.description_("Expensive Chocolate").get.head === orderId


    // Touch entity ................................

    // Get all attributes/values of this entity. Sub-component values are recursively retrieved and sorted by their ids
    orderId.touch === Map(
      ":db/id" -> 17592186045421L,
      ":order/lineItems" -> List(
        Map(
          ":db/id" -> 17592186045422L,
          ":lineItem/price" -> 48.0,
          ":lineItem/product" -> Map(
            ":db/id" -> 17592186045418L,
            ":product/description" -> "Expensive Chocolate"),
          ":lineItem/quantity" -> 1),
        Map(
          ":db/id" -> 17592186045423L,
          ":lineItem/price" -> 38.0,
          ":lineItem/product" -> Map(
            ":db/id" -> 17592186045419L,
            ":product/description" -> "Cheap Whisky"),
          ":lineItem/quantity" -> 2)))


    // Retract nested data ............................

    // Retract entity - all subcomponents/lineItems are retracted
    orderId.retract

    // The products are still there
    Product.e.description_("Expensive Chocolate" or "Cheap Whisky").get === List(chocolateId, whiskyId)
  }
}