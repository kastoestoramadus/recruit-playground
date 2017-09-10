package walidus.stock

import org.scalatest.{FlatSpec, Matchers}
import walidus.stock.model._

class OrderBookTest extends FlatSpec with Matchers{
  val o1 = BuyLimitOrder(1, 14, 20)
  val o2 = BuyIcebergOrder(2, 15, 50, 20)
  val o3 = SellLimitOrder(3, 16, 15)
  val baseOrders = Seq(o1, o2, o3)
  val expectedOrders = Orders(List(o2, o1), List(o3))

  "Ordering" should "handle Buy orders" in {
    val item = o2.copy(id = 7, price =15)
    OrderBook.placeNewItem(
      item,
      List(o1, o2, o2.copy(id = 5), o2.copy(id = 6, price = 16)).reverse
    ).indexOf(item) shouldBe 3
  }

  "OrderBook" should "stack up the orders" in {
    val result = baseOrders.foldLeft((OrderBook.empty, Seq[Transaction]())){
      (orderBookAndTransactions, order) =>
        orderBookAndTransactions._1.placeOrder(order)
    }
    val resultOrders = result._1.storedOrders
    resultOrders.buyList shouldBe expectedOrders.buyList
    resultOrders.sellList shouldBe expectedOrders.sellList
    result._2 shouldBe empty
  }

  "OrderBook" should "execute transactions for matched new Sell order" in {
    val o4 = SellLimitOrder(4, 13, 60)
    val result = OrderBook(expectedOrders).placeOrder(o4)

    val resultOrders = result._1.storedOrders

    resultOrders.buyList.head.quantity shouldBe 10
    resultOrders.sellList.head.quantity shouldBe 15
    result._2.size shouldBe 4
    result._2.last shouldBe Transaction(1, 4, 14, 10)
  }

  "OrderBook" should "execute Iceberg nicely with other order with same price" in {
    val orders = List(o2, o1.copy(price = o2.price))
    val o4 = SellLimitOrder(4, 13, 60)
    val result = OrderBook(Orders(orders,Nil)).placeOrder(o4)

    val resultOrders = result._1.storedOrders
    resultOrders.buyList.head.quantity shouldBe 10
    result._2.last shouldBe Transaction(2, 4, 15, 20)
  }

  "OrderBook" should "execute transactions for matched new Buy order" in {
    val o1 = SellLimitOrder(1, 15, 20)
    val o2 = SellIcebergOrder(2, 14, 50, 20)
    val o3 = BuyLimitOrder(3, 13, 15)

    val expectedOrders = Orders(List(o3), List(o2, o1))
    val o4 = BuyLimitOrder(4, 16, 60)
    val finalOrderBook = OrderBook(expectedOrders).placeOrder(o4)

    val remainingOrders = finalOrderBook._1.storedOrders
    println(finalOrderBook._2)

    remainingOrders.buyList.head.quantity shouldBe 15
    remainingOrders.sellList.head.quantity shouldBe 10
    finalOrderBook._2.size shouldBe 4
    finalOrderBook._2.last shouldBe Transaction(1, 4, 15, 10)
  }
}
