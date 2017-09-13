package walidus.stock.model

case class Transaction(buyOrderId: Id, sellOrderId: Id, price: Int, quantity: Int)