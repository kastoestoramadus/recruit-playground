package walidus.stock.model

// list order is important, relates to time of placing an order
// FIXME compiler checks are missing. Would need to push further more generics. ~(with Buy/Sale)
case class Orders(buyList: List[Order], sellList: List[Order])

object Orders {
  val empty: Orders = Orders(Nil, Nil)
}