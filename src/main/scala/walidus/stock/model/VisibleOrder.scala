package stock.model

import walidus.stock.model.{Id, Order, Orders}

case class VisibleOrders(buyOrders: List[VisibleOrder], sellOrders: List[VisibleOrder])
object VisibleOrders {
  def apply(orders: Orders): VisibleOrders = VisibleOrders(
    orders.buyList.map(VisibleOrder.apply),
    orders.sellList.map(VisibleOrder.apply)
  )
}

case class VisibleOrder(id: Id, price: Int, quantity: Int)
object VisibleOrder {
  def apply(o: Order): VisibleOrder = {
    VisibleOrder(o.id, o.price, o.stepQuantity)
  }
}