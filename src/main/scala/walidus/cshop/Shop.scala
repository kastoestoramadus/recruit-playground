package walidus.cshop

object Shop {
  val costs: Map[Item, Bill] = Map(
    Orange -> 25,
    Apple -> 60,
    Banana -> 20
  )
  def promotionCounting(order: Order): Order = {
    orangePromotion(
      bananaAndApplePromotion(order)
    )
  }
  def makeBill(order: Order): Bill =
    promotionCounting(order)
      .map(pair => costs(pair._1)*pair._2) // count prices with quantities
      .sum

  def orangePromotion(order: Order): Order = {
    order.map{ case (item, amount) => {
      val newAmount = item match {
        case Orange => (amount/3)*2 + amount%3
        case _ => amount
      }
      item -> newAmount
    }}
  }
  def bananaAndApplePromotion(order: Order): Order = {
    val bananas = order.getOrElse(Banana,0)
    val apples = order.getOrElse(Apple, 0)
    val totalAmount = apples + bananas
    val freeFruits = totalAmount/2
    val (moreExpensive, lessExpensive) = {
      val f: (Item, Bill) = (Apple, apples)
      val s: (Item, Bill) = (Banana, bananas)
      if(f._2 > s._2)
        (f, s)
      else
        (s, f)
    }

    val remainFree: Bill = freeFruits - lessExpensive._2

    val more = moreExpensive._1 -> (moreExpensive._2 - Math.max(remainFree,0))
    val less = lessExpensive._1 -> Math.max(-remainFree,0)

    order.map{ case (item, amount) => {
      if(item == moreExpensive._1) more
      else if (item == lessExpensive._1) less
      else item -> amount
    }}
  }
}
