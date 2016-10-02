package walidus.cshop

import org.scalatest.{FlatSpec, Matchers}

class ShopTest extends FlatSpec with Matchers{
  "Shop" should "match with example" in {
    Shop.makeBill(Seq(Apple, Apple, Orange, Apple)) shouldEqual 205
  }
  "Shop" should "match with symmetric example" in {
    Shop.makeBill(Seq(Orange, Orange, Apple, Orange)) shouldEqual 135
  }
}
