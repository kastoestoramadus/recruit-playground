package walidus.stock

import play.api.libs.json.Json
import stock.model.{VisibleOrder, VisibleOrders}
import walidus.stock.model._

import scala.util.Try

object Formatters {
  implicit val rOFFormatter= Json.format[RawOrder]
  implicit val tROFFormatter= Json.format[TypedRawOrder]
  implicit val vOFormatter= Json.format[VisibleOrder]
  implicit val oDTOFormatter= Json.format[VisibleOrders]
  implicit val tFormatter= Json.format[Transaction]

  def orderFromJson(str: String): Try[Order] = Try{
    val json = Json.parse(str)
    tROFFormatter.reads(json).map( raw => {
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
}
case class TypedRawOrder(`type`: String, order: RawOrder)
case class RawOrder(direction: String, id: Int, price: Int, quantity: Int, peak: Option[Int])