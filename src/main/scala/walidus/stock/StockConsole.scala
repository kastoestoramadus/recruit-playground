package walidus.stock

import walidus.stock.model._

object StockConsole extends App{

  println("Welcome to stock Console. You are connected to the OrderBook. Type new orders. Examples of orders:")
  println("{“type”: “Limit”, “order”: {“direction”: “Buy”, “id”: 1, “price”: 14, “quantity”: 20}}")
  println("{“type”: “Iceberg”, “order”: {“direction”: “Sell”, “id”: 2, “price”: 15, “quantity”: 50, “peak”: 20}}")

  var orderBook = OrderBook.empty

  while(true) {
    println("Place new order.")

    val typed: Order = getNext()
    val (newBook, transactions) = orderBook.placeOrder(typed)

    printAsJson(toDTO(newBook.remainingOrders))
    printAsJson(transactions)

    orderBook = newBook
  }

  def getNext(): Order = new SellLimitOrder(1, 2, 3) // FIXME
  def toDTO(orders: Orders): OrdersDTO = ???
  def printAsJson(o: OrdersDTO): Unit = ???
  def printAsJson(transactions: Seq[Transaction]): Unit = ???

  def toVisibleOrders(orders: Seq[Order]): Seq[VisibleOrder] = orders.map{ o => o match {
    case i: Iceberg => VisibleOrder(i.id, i.price, Math.min(i.quantity, i.peak))
    case l: Limit => VisibleOrder(l.id, l.price, l.quantity)
  }}
}

case class OrdersDTO(buyOrders: List[VisibleOrder], sellOrders: List[VisibleOrder])
case class VisibleOrder(id: Id, price: Int, quantity: Int)