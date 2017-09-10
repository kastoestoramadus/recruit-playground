package walidus.stock.model

case class OrderBook(remainingOrders: Orders) {
  import OrderBook._
  def placeOrder(order: Order): (OrderBook, Seq[Transaction]) =
    order match {
      case b: Buy  =>
        (OrderBook(Orders(
          placeNewItem(b, remainingOrders.buyList), remainingOrders.sellList)),
          Nil
        )
      case s: Sell =>
        var result: MatchingAgg = MatchingAgg(Some(s), Nil, remainingOrders.buyList)
        var cycleResult: MatchingAgg = null
        do {
          val (group, rest) = detachFirstGroup(result.remaining)
          cycleResult = processOneCycleForGroup(result.inProcess.get, group)
          result = MatchingAgg(cycleResult.inProcess,
            result.transactions ++ cycleResult.transactions ,
            cycleResult.remaining ++ rest)
        } while (cycleResult.transactions.nonEmpty && cycleResult.inProcess.isDefined)

        summaryAfterNewSaleO(result)
    }

  def summaryAfterNewSaleO(update: MatchingAgg): (OrderBook, Seq[Transaction]) = {
    val newSellO = update.inProcess
      .map( placeNewItem(_, remainingOrders.sellList))
      .getOrElse(remainingOrders.sellList)

    val newBuyO = update.remaining
    (OrderBook(Orders(newBuyO, newSellO)), update.transactions)
  }

}

object OrderBook {
  val empty = OrderBook(Orders.empty)

  // _1 - first group, _2 - rest
  def detachFirstGroup(orders: List[Order]): (List[Order], List[Order]) =
    orders.span(_.price == orders.head.price)

  private def processOneCycleForGroup(s: Order, buyGroup: List[Order]) = {
    if(buyGroup.isEmpty || s.price > buyGroup.head.price)
      MatchingAgg(Some(s), Nil, buyGroup)
    else
      buyGroup.foldLeft(MatchingAgg(Some(s), Nil, Nil)) { (agg, b) => agg.inProcess match {
          case Some(s) =>
            val qT = Math.min(getStepQuantity(s),getStepQuantity(b))
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
  def getStepQuantity(order: Order) = order match {
    case _: Limit => order.quantity
    case i: Iceberg => Math.min(i.peak, i.quantity)
  }

  def placeNewItem(newO: Order, l: List[Order]): List[Order] = {
    val comp = getComparator(newO)
    val (before, after) = l.span(oldO => comp(newO.price, oldO.price))
    before ++ List(newO) ++ after
  }

  def getComparator(o: Order): (Int, Int) => Boolean = o match {
    case s: Sell => _ >= _
    case b: Buy => _ <= _
  }

  case class MatchingAgg(inProcess: Option[Order], transactions: List[Transaction], remaining: List[Order])
}
