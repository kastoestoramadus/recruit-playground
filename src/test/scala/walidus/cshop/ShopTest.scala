package walidus.cshop

import org.scalatest.{FlatSpec, Matchers}

class ShopTest extends FlatSpec with Matchers{
  "Shop" should "match with simple example" in {
    Shop.makeBill(Seq(Apple, Orange)) shouldEqual 85
  }
  "Shop" should "give discount for oranges" in {
    Shop.makeBill(Seq(Orange, Orange, Apple, Orange)) shouldEqual 110
  }
  "Shop" should "give free apple for one apple" in {
    Shop.makeBill(Seq(Apple, Apple, Orange, Apple)) shouldEqual 145
  }
}
