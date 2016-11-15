/**
 * Created by Admin on 9/23/2015.
 */
class utilityFunctions extends Serializable{

  def toTuple(stockLine: List[String]): List[(String, String)] = for {
    itemPair <- stockLine
    itemArray = itemPair.split("=")
  } yield (itemArray(0), itemArray(1))


}
