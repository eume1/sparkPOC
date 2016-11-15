


/**
 * Created by Emeka Ume(emeka.ume@accenture.com) on 9/4/2015.
 */

case class stockPriceInfo(var ticker: String, var dateTime: String, var ask: Double,
                          var bid: Double, var previousClose: Double, var volume: Long)

object stockPriceInfo extends Serializable{
  val dateDelim = "-"
  val timeDelim = ":"

  //(Ticker,(DataTime,price))
  def extractRelevantStockData(stockInfo: Map[String, String]): stockPriceInfo = {
    val ticker = stockInfo.getOrElse("symbol", "----")
    val year = stockInfo.getOrElse("YEAR", "1900")
    val month = stockInfo.getOrElse("MONTH", 0)
    val day = stockInfo.getOrElse("DAY_OF_MONTH", 0)
    val hourOfDay = stockInfo.getOrElse("HOUR_OF_DAY", 0)
    val minute = stockInfo.getOrElse("MINUTE", 0)
    val second = stockInfo.getOrElse("SECOND", 0)
    val askPrice = stockInfo.getOrElse("ask", "0.0").toDouble
    val bidPrice = stockInfo.getOrElse("bid", "0.0").toDouble
    val askSize = stockInfo.getOrElse("askSize", "0").toLong
    val volume = stockInfo.getOrElse("volume", "0").toLong
    val previousClose = stockInfo.getOrElse("previousClose", "0.0").toDouble
    val averageVolume = stockInfo.getOrElse("avgVolume", "0.0").toLong
    val date = year + dateDelim + month + dateDelim + day
    val time = hourOfDay + timeDelim + minute + timeDelim + second
    val dateTime = date + "@" + time


    //Some(stockPriceInfo(ticker,dateTime,askPrice,bidPrice, askPrice, volume))
    stockPriceInfo(ticker, dateTime, askPrice, bidPrice, previousClose, volume)
  }

  //def apply & unapply are automatically added
  /*  def apply(ticker:String, dateTime:String, ask:Double, bid:Double, previousClose:Double, volume:Long) =
      new stockPriceInfo(ticker:String, dateTime:String, ask:Double, bid:Double, previousClose:Double, volume:Long)

    def unapply(s:stockPriceInfo): Option[Tuple6[String,String,Double,Double,Double,Long]]=
      Some((s.ticker,s.dateTime,s.ask, s.bid, s.previousClose, s.volume))*/


}

