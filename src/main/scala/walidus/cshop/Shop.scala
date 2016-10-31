package walidus.cshop

object Shop {
  val costs: Map[Item, Bill] = Map(
    Orange -> 25,
    Apple -> 60,
    Banana -> 20
  )
  def promotionCounting(order: Order): Order = {
    order ++ orangePromotion(order) ++ bananaAndApplePromotion(order)
  }
  def makeBill(order: Order): Bill =
    promotionCounting(order)
      .map(pair => costs(pair._1)*pair._2) // count prices with quantities
      .sum

  def orangePromotion(order: Order): Order = {
    val amount = order(Orange)
    Map(Orange -> ((amount/3)*2 + amount%3))
  }
  def bananaAndApplePromotion(order: Order): Order = {
    val applicatives = Seq(
      Banana -> order(Banana),
      Apple -> order(Apple)
    ).sortBy(_._2).reverse

    val totalAmount = applicatives.foldLeft(0)(_+_._2)
    val freeFruits = totalAmount/2
    val moreExp = applicatives.head
    val lessExp = applicatives.tail.head

    val remainedFree = freeFruits - lessExp._2

    val newValOfMoreExp = moreExp.copy(_2 = moreExp._2 - Math.max(remainedFree,0))
    val newValOfLessExp = lessExp.copy(_2 = Math.max(-remainedFree,0))

    Map(newValOfMoreExp, newValOfLessExp)
  }
}
