package walidus.stock.model

case class OrderBook(storedOrders: Orders) {
  import OrderBook._

  def placeOrder(order: Order): (OrderBook, Seq[Transaction]) = {
    order.direciton match {
      case Buy =>
        val update: MatchingAgg = calculateNewState(order, storedOrders.sellList)

        val newBuyO = addIfPresent(update.inProcess, storedOrders.buyList)
        val newSellO = update.remaining

        (OrderBook(Orders(newBuyO, newSellO)), update.transactions)
      case Sell =>
        val update: MatchingAgg = calculateNewState(order, storedOrders.buyList)

        val newSellO = addIfPresent(update.inProcess,  storedOrders.sellList)
        val newBuyO = update.remaining

        (OrderBook(Orders(newBuyO, newSellO)), update.transactions)
    }
  }

  def addIfPresent(el: Option[Order], to: List[Order]): List[Order] =
    el.map( placeNewItem(_, to))
      .getOrElse(to)

}

object OrderBook {
  val empty = OrderBook(Orders.empty)

  // FIXME should be recursive
  private def calculateNewState(s: Order, remainingOrders: List[Order]) = {
    var aggregate: MatchingAgg = MatchingAgg(Some(s), Nil, remainingOrders)
    var cycleResult: MatchingAgg = null
    do {
      val (group, rest) = detachFirstGroup(aggregate.remaining)
      cycleResult = processOneCycleForGroup(aggregate.inProcess.get, group)
      aggregate = MatchingAgg(cycleResult.inProcess,
        cycleResult.transactions ++ aggregate.transactions,
        cycleResult.remaining ++ rest)
    } while (cycleResult.transactions.nonEmpty && cycleResult.inProcess.isDefined)
    aggregate.copy(transactions = aggregate.transactions.reverse)
  }

  // _1 - first group, _2 - rest
  def detachFirstGroup(orders: List[Order]): (List[Order], List[Order]) =
    orders.span(_.price == orders.head.price)

  private def processOneCycleForGroup(newOrder: Order, group: List[Order]) = {
    if(group.isEmpty || !isProfitable(newOrder, group.head))
      MatchingAgg(Some(newOrder), Nil, group)
    else
      group.foldLeft(MatchingAgg(Some(newOrder), Nil, Nil)) { (agg, b) => agg.inProcess match {
          case Some(s) =>
            val qT = Math.min(s.stepQuantity, b.stepQuantity)
            val processingOrderUpdate = {
              val tmp = s.quantity - qT
              if(tmp == 0) None
              else Some(s.updateQuantityTo(tmp) )
            }
            val remainingOrders = {
              val tmp = b.quantity - qT
              if(tmp == 0) agg.remaining
              else b.updateQuantityTo(tmp) :: agg.remaining
            }
            val newTransaction = Transaction(b.id, s.id, b.price, qT)

            MatchingAgg(processingOrderUpdate, newTransaction :: agg.transactions, remainingOrders)
          case None => MatchingAgg(None, agg.transactions, b :: agg.remaining)
        }
      }
  }

  // FIXME how to set both Orders should be the same DirectionType?
  def placeNewItem(newO: Order, l: List[Order]): List[Order] = {
    val comp = getComparator(newO)
    val (before, after) = l.span(oldO => !comp(newO.price, oldO.price))
    before ++ List(newO) ++ after
  }

  def getComparator(o: Order): (Int, Int) => Boolean = o.direciton match {
    case Sell => _ < _
    case Buy => _ > _
  }

  def isProfitable(first: Order, second: Order): Boolean = (first.direciton, second.direciton) match {
    case (Buy, Sell) => first.price >= second.price
    case (Sell, Buy) => first.price <= second.price
    case default => assert(false, default); ??? // ugly, compiler should forbid such cases
  }
  // FIXME how to set both Orders should be an opposite DirectionTypes?
  case class MatchingAgg(inProcess: Option[Order], transactions: List[Transaction], remaining: List[Order])
}
