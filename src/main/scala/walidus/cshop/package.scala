package walidus

package object cshop {
  implicit def seqItem2Order(order: Seq[Item]): Order =
    order.groupBy(identity)
      .map(pair => (pair._1, pair._2.size))
      .withDefaultValue(0)

  type Order = Map[Item, Int]

  type Bill = Int // in pennies
}
