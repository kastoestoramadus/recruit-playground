package walidus.stock

import org.scalatest.{FlatSpec, Matchers}
import walidus.stock.model._

class StockConsoleTest extends FlatSpec with Matchers{
  "Deserializing" should "produce LimitOrder" in {
    StockConsole.orderFromJson(
      """{"type": "Limit", "order": {"direction": "Sell", "id": 1, "price": 14, "quantity": 20}}"""
    ).get shouldBe LimitOrder(Sell, 1, 14, 20)
  }
  "Deserializing" should "produce IcebergOrder" in {
    StockConsole.orderFromJson(
      """{"type": "Iceberg", "order": {"direction": "Buy", "id": 2, "price": 15, "quantity": 50, "peak": 20}}"""
    ).get shouldBe IcebergOrder(Buy ,2, 15, 50, 20)
  }
}
