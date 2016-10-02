package walidus.cshop

import org.scalatest.{FlatSpec, Matchers}

class ShopTest extends FlatSpec with Matchers{
  "Shop" should "match with expample" in {
    Shop.makeBill(Seq(Orange, Orange, Apple, Orange)) shouldEqual 205
  }
}
