package walidus.cshop

object ShopTerminalInterface {
  def main(args: Array[String]): Unit = {
    val order = args.flatMap(Item.parseItem).toSeq
    val msg: String = if(args.length == order.length)
      s"Bill for order: ${args.mkString("")} is ${Shop.makeBill(order)}" // TODO could be tested
    else
      s"Type Order with possible values: Orange, Apple. Separator is ' '." // TODO not hardwired

    println(msg)
  }
  def bill2String(b: Bill): String = s"£${b.toDouble/100}"
}
