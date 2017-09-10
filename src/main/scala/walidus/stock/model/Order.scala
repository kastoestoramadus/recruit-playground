package walidus.stock.model

/* Direction and Types not as field done for experiment.
*  With fields and more OrderTypes would be much less classes and serialization would be easier.
*  In current way could use more compiler help.
*/
sealed trait Order {
  this: OrderType with OrderDirection  =>
  def updateQuantityTo(q: Int): Order // TODO shapeless

  val id: Id
  val price: Int
  val quantity: Int
}

case class SellLimitOrder(id: Id, price: Int, quantity: Int)
  extends Order with Sell with Limit {
  override def updateQuantityTo(q: Id) = this.copy(quantity = q)
}

case class SellIcebergOrder(id: Id, price: Int, quantity: Int, peak: Int)
  extends Order with Sell with Iceberg {
  override def updateQuantityTo(q: Id) = this.copy(quantity = q)
}

case class BuyLimitOrder(id: Id, price: Int, quantity: Int)
  extends Order with Buy with Limit {
  override def updateQuantityTo(q: Id) = this.copy(quantity = q)
}

case class BuyIcebergOrder(id: Id, price: Int, quantity: Int, peak: Int)
  extends Order with Buy with Iceberg {
  override def updateQuantityTo(q: Id) = this.copy(quantity = q)
}

sealed trait OrderType
trait Limit extends OrderType
trait Iceberg extends OrderType {
  def peak: Int
}

sealed trait OrderDirection
trait Buy extends OrderDirection
trait Sell extends OrderDirection

// list order is important, relates to time of placing an order
// FIXME compiler checks are missing. Would need to push further more generics. ~(with Buy/Sale)
case class Orders(buyList: List[Order], sellList: List[Order])

object Orders {
  val empty: Orders = Orders(Nil, Nil)
}

case class Transaction(buyOrderId: Id, sellOrderId: Id, price: Int, quantity: Int)