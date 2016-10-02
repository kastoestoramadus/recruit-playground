package walidus.cshop

object Shop {
  val costs: Map[Item, Bill] = Map(
    Orange -> 25,
    Apple -> 60
  )
  def promotionCounting(order: Order): Order = {
    order.map{ case (item, amount) => {
      val newAmount = item match {
        case Orange => (amount/3)*2 + amount%3
        case Apple => amount/2 + amount%2
      }
      item -> newAmount
    }} // TODO make it prettier
  }
  def makeBill(order: Order): Bill =
    promotionCounting(order)
      .map(pair => costs(pair._1)*pair._2) // count prices with quantities
      .sum
}
