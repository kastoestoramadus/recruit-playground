package walidus.cshop

sealed trait Item

case object Apple extends Item
case object Orange extends Item
case object Banana extends Item

object Item {
  def parseItem(s: String): Option[Item] = s match {
    case "Apple" => Some(Apple) // TODO not hardwired
    case "Orange" => Some(Orange)
    case "Banana" => Some(Banana)
    case _ => None
  }
}