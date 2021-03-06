package walidus.stock.model

import stock.model.VisibleOrders

case class OrderBook(private[stock] val storedOrders: Orders) {
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

  def ordersForUsers: VisibleOrders = VisibleOrders(storedOrders)
}

object OrderBook {
  val empty = OrderBook(Orders.empty)

  private def addIfPresent(el: Option[Order], to: List[Order]): List[Order] =
    el.map( placeNewItem(_, to))
      .getOrElse(to)

  // FIXME how to set both Orders should be the same DirectionType?
  private[model] def placeNewItem(newO: Order, l: List[Order]): List[Order] = {
    val comp = getComparator(newO)
    val (before, after) = l.span(oldO => !comp(newO.price, oldO.price))
    before ++ List(newO) ++ after
  }

  private def getComparator(o: Order): (Int, Int) => Boolean = o.direciton match {
    case Sell => _ < _
    case Buy => _ > _
  }

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
  private def detachFirstGroup(orders: List[Order]): (List[Order], List[Order]) =
    orders.span(_.price == orders.head.price)

  // group with same price, assumption: ordered by placing times
  private def processOneCycleForGroup(newOrder: Order, group: List[Order]) = {
    if(group.isEmpty || !isProfitable(newOrder, group.head))
      MatchingAgg(Some(newOrder), Nil, group)
    else
      group.foldLeft(MatchingAgg(Some(newOrder), Nil, Nil)) { (agg, b) => agg.inProcess match {
          case Some(s) =>
            val transactionQuantity = Math.min(s.stepQuantity, b.stepQuantity)
            val processingOrderUpdate = {
              val tmp = s.quantity - transactionQuantity
              if(tmp == 0) None
              else Some(s.updateQuantityTo(tmp) )
            }
            val unmatchedOrders = {
              val tmp = b.quantity - transactionQuantity
              if(tmp == 0) agg.remaining
              else b.updateQuantityTo(tmp) :: agg.remaining
            }
            val newTransaction = Transaction(b.id, s.id, b.price, transactionQuantity)

            MatchingAgg(processingOrderUpdate, newTransaction :: agg.transactions, unmatchedOrders)
          case None => MatchingAgg(None, agg.transactions, b :: agg.remaining)
        }
      }
  }

  private def isProfitable(first: Order, second: Order): Boolean = (first.direciton, second.direciton) match {
    case (Buy, Sell) => first.price >= second.price
    case (Sell, Buy) => first.price <= second.price
    case default => assert(false, default); ??? // ugly, compiler should forbid such cases
  }
  // FIXME how to set both Orders should be an opposite DirectionTypes?
  private case class MatchingAgg(inProcess: Option[Order], transactions: List[Transaction], remaining: List[Order])
}
