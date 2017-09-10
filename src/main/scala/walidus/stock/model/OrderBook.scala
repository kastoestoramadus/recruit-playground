package walidus.stock.model

case class OrderBook(remainingOrders: Orders) {
  import OrderBook._
  def placeOrder(order: Order): (OrderBook, Seq[Transaction]) = order match {
    case b: Buy  =>
      (OrderBook(Orders(
        remainingOrders.buyOrders :+ b,remainingOrders.sellOrders)),
        Nil
      )
    case s: Sell =>
      val (group, rest) = detachFirstGroup(remainingOrders.buyOrders)
      val result = processNewOrder(s, group)
      (OrderBook(Orders(
        remainingOrders.buyOrders, remainingOrders.sellOrders :+ s)),
        Nil
      )
  }

  private def processNewOrder(s: Order with Sell, group: Seq[Order]) = {
    group.foldLeft(MatchingAgg(s, Nil, Nil)) { (agg, b) =>
      agg // FIXME
    }
  }
  case class MatchingAgg(newOrder: Order, transactions: Seq[Transaction], remaining: Seq[Order])
}

object OrderBook {
  val empty = OrderBook(Orders.empty)

  // _1 - first group, _2 - rest
  def detachFirstGroup(orders: Seq[Order]): (Seq[Order], Seq[Order]) =
    orders.span(_.price == orders.head.price)
}

// list order is important, relates to time of placing an order
case class Orders(buyOrders: List[Order], sellOrders: List[Order])

object Orders {
  val empty: Orders = Orders(Nil, Nil)
}

case class Transaction(buyOrderId: Id, sellOrderId: Id, price: Int, quantity: Int)
