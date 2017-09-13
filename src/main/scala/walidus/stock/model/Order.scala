package walidus.stock.model

sealed trait Order {
  val direciton: OrderDirection
  val id: Id
  val price: Int
  val quantity: Int

  def updateQuantityTo(q: Int): Order // Fixme with shapeless

  def stepQuantity: Int = quantity
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