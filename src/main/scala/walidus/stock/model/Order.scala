package walidus.stock.model

sealed trait Order {
  def updateQuantityTo(q: Int): Order // Fixme with shapeless
  def stepQuantity: Int = quantity

  val direciton: OrderDirection
  val id: Id
  val price: Int
  val quantity: Int
}

case class LimitOrder(direciton: OrderDirection, id: Id, price: Int, quantity: Int)
  extends Order with Limit {
  override def updateQuantityTo(q: Id) = this.copy(quantity = q)
}

case class IcebergOrder(direciton: OrderDirection, id: Id, price: Int, quantity: Int, peak: Int)
  extends Order with Iceberg {
  override def updateQuantityTo(q: Id) = this.copy(quantity = q)
  override def stepQuantity: Int = Math.min(peak, quantity)
}

sealed trait OrderType
trait Limit extends OrderType
trait Iceberg extends OrderType {
  def peak: Int
}

sealed trait OrderDirection
object Buy extends OrderDirection
object Sell extends OrderDirection

// list order is important, relates to time of placing an order
// FIXME compiler checks are missing. Would need to push further more generics. ~(with Buy/Sale)
case class Orders(buyList: List[Order], sellList: List[Order])

object Orders {
  val empty: Orders = Orders(Nil, Nil)
}

case class Transaction(buyOrderId: Id, sellOrderId: Id, price: Int, quantity: Int)