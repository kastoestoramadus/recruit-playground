package walidus.cshop

import org.scalatest.{FlatSpec, Matchers}

class ShopTest extends FlatSpec with Matchers{
  "Shop" should "match with simple example" in {
    Shop.makeBill(Seq(Apple, Orange)) shouldEqual 85
  }
  "Shop" should "give discount for oranges" in {
    Shop.makeBill(Seq(Orange, Orange, Orange)) shouldEqual 50
  }
  "Shop" should "give free apple for one apple" in {
    Shop.makeBill(Seq(Apple, Apple)) shouldEqual 60
  }
  "Shop" should "pass complex example" in {
    Shop.makeBill(Seq(Orange, Orange, Apple, Orange)) shouldEqual 110
  }
  "Shop" should "pass second complex example" in {
    Shop.makeBill(Seq(Apple, Apple, Orange, Apple, Orange, Orange)) shouldEqual 170
  }
  "Shop" should "work well on Apples and Bananas" in {
    Shop.makeBill(Seq(Apple, Apple, Banana, Apple, Banana)) shouldEqual 180
  }
}
