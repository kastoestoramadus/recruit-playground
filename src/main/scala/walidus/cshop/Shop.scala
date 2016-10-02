package walidus.cshop

object Shop {
  val costs: Map[Item, Bill] = Map(
    Orange -> 25,
    Apple -> 60
  )
  def makeBill(order: Order): Bill = order.map(costs).reduce(_+_)
}
