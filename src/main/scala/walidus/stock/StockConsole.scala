package walidus.stock

import play.api.libs.json._
import walidus.stock.model._

import scala.io.StdIn
import scala.util.Try

object StockConsole extends App{

  println("""Welcome to stock Console. You are connected to the OrderBook. Examples of orders:""")
  println("""{"type": "Limit", "order": {"direction": "Buy", "id": 1, "price": 14, "quantity": 20}}""")
  println("""{"type": "Iceberg", "order": {"direction": "Buy", "id": 2, "price": 15, "quantity": 50, "peak": 20}}""")
  println("""{"type": "Limit", "order": {"direction": "Sell", "id": 3, "price": 16, "quantity": 15}}""")
  println("""{"type": "Limit", "order": {"direction": "Sell", "id": 4, "price": 13, "quantity": 60}}""")
  print("Type new orders:\n>")

  var orderBook = OrderBook.empty
  Iterator.continually(StdIn.readLine())
    .takeWhile(line => line != null && line.nonEmpty)
    .map(orderFromJson)
    .filter(_.isSuccess).map(_.get)
    .foreach(typed => {
      val (newBook, transactions) = orderBook.placeOrder(typed)

      printAsJson(OrdersDTO(newBook.storedOrders))
      printAsJson(transactions)
      print('>')

      orderBook = newBook
    })

  def orderFromJson(str: String): Try[Order] = Try{
    val json = Json.parse(str)
    Formatters.tROF.reads(json).map( raw => {
      val o = raw.order
      val dir = o.direction match {
        case "Buy" => Buy
        case "Sell" => Sell
      }
      raw.`type` match {
        case "Limit" => LimitOrder(dir, o.id, o.price, o.quantity)
        case "Iceberg" => IcebergOrder(dir, o.id, o.price, o.quantity, o.peak.get)
      }
    }).get
  }

  def printAsJson(o: OrdersDTO): Unit = {
    println(Formatters.oDTO.writes(o))
  }
  def printAsJson(transactions: Seq[Transaction]): Unit =
    transactions.map(Formatters.t.writes).foreach(println)

  def toVisibleOrders(orders: Seq[Order]): Seq[VisibleOrder] = orders.map{ o => o match {
    case i: Iceberg => VisibleOrder(i.id, i.price, Math.min(i.quantity, i.peak))
    case l: Limit => VisibleOrder(l.id, l.price, l.quantity)
  }}
}

case class OrdersDTO(buyOrders: List[VisibleOrder], sellOrders: List[VisibleOrder])
object OrdersDTO {
  def apply(orders: Orders): OrdersDTO = OrdersDTO(
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

case class TypedRawOrder(`type`: String, order: RawOrder)
object Formatters {
  implicit val rOF = Json.format[RawOrder]
  implicit val tROF = Json.format[TypedRawOrder]
  implicit val vO = Json.format[VisibleOrder]
  implicit val oDTO = Json.format[OrdersDTO]
  implicit val t = Json.format[Transaction]
}
case class RawOrder(direction: String, id: Int, price: Int, quantity: Int, peak: Option[Int])
object RawOrder {
}