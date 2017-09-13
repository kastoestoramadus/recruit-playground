package walidus.stock

import stock.model.VisibleOrders
import walidus.stock.model._

import scala.io.StdIn

object StockConsole extends App{
  import Formatters._
  println("""Welcome to stock Console. You are connected to the OrderBook. Examples of orders:""")
  println("""{"type": "Limit", "order": {"direction": "Buy", "id": 1, "price": 14, "quantity": 20}}""")
  println("""{"type": "Iceberg", "order": {"direction": "Buy", "id": 2, "price": 15, "quantity": 50, "peak": 20}}""")
  println("""{"type": "Limit", "order": {"direction": "Sell", "id": 3, "price": 16, "quantity": 15}}""")
  println("""{"type": "Limit", "order": {"direction": "Sell", "id": 4, "price": 13, "quantity": 60}}""")
  println("Type new orders:")

  var orderBook = OrderBook.empty
  Iterator.continually(StdIn.readLine())
    .takeWhile(line => line != null && line.nonEmpty)
    .map(orderFromJson)
    .filter(_.isSuccess).map(_.get)
    .foreach(typed => {
      val (newBook, transactions) = orderBook.placeOrder(typed)

      printAsJson(newBook.ordersForUsers)
      printAsJson(transactions)

      orderBook = newBook
    })

  def printAsJson(o: VisibleOrders): Unit = println(oDTOFormatter.writes(o))
  def printAsJson(transactions: Seq[Transaction]): Unit = transactions.map(tFormatter.writes).foreach(println)
}