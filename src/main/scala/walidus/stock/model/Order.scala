package walidus.stock.model

abstract sealed class Order(val id: Id, val price: Int, val quantity: Int) {
  this: OrderType with OrderDirection  =>
}

case class SellLimitOrder(override val id: Id, override val price: Int, override val quantity: Int)
  extends Order(id, price, quantity)
  with Sell with Limit

case class SellIcebergOrder(override val id: Id, override val price: Int, override val quantity: Int, peak: Int)
  extends Order(id, price, quantity)
  with Sell with Iceberg

case class BuyLimitOrder(override val id: Id, override val price: Int, override val quantity: Int)
  extends Order(id, price, quantity)
  with Buy with Limit

case class BuyIcebergOrder(override val id: Id, override val price: Int, override val quantity: Int, peak: Int)
  extends Order(id, price, quantity)
  with Buy with Iceberg

sealed trait OrderType
trait Limit extends OrderType
trait Iceberg extends OrderType {
  def peak: Int
}

sealed trait OrderDirection
trait Buy extends OrderDirection
trait Sell extends OrderDirection