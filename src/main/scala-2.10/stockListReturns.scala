/**
 * Created by Emeka Ume, emeka.ume@accenture.com on 9/20/2015.
 */


case class stockListReturns(mean: Double, stdev: Double, high: Double, low: Double, meanVol: Long)

object stockListReturns extends Serializable{

  def result(s: (String, Iterable[(String, Iterable[Option[stockPriceInfo]])])): stockListReturns = {
    lazy val listOfPreviousClosePrices: List[Double] = s._2.map(_._2).reduce(_ ++ _).map(_.get.previousClose).toList
    lazy val listOfAskVolume: List[Long] = s._2.map(_._2).reduce(_ ++ _).map(_.get.volume).toList
    lazy implicit val mean: Double = listOfPreviousClosePrices.sum / listOfPreviousClosePrices.size
    lazy val stdev: Double = calcStdev(mean, listOfPreviousClosePrices)
    lazy val high: Double = listOfPreviousClosePrices.max
    lazy val low: Double = listOfPreviousClosePrices.min
    lazy val meanVol: Long = listOfAskVolume.sum / listOfAskVolume.size

    stockListReturns.apply(mean, stdev, high, low, meanVol)
  }

  //Used for Testing
  def result(s: Iterable[Double], ss: Iterable[Int]): stockListReturns = {
    lazy val listOfAskPrices: List[Double] = s.map(x => x).toList
    lazy val listOfAskVolume: List[Int] = ss.map(x => x).toList
    lazy implicit val mean: Double = listOfAskPrices.sum / listOfAskPrices.size
    lazy val stdev: Double = calcStdev(mean, listOfAskPrices)
    lazy val high: Double = listOfAskPrices.max
    lazy val low: Double = listOfAskPrices.min
    lazy val meanVol: Long = listOfAskVolume.sum / listOfAskVolume.size
    stockListReturns.apply(mean, stdev, high, low, meanVol)
  }

  def calcStdev(implicit mean: Double, s: Iterable[Double]): Double = {
    val sqrdDevFromMean: Double = s.map(x => (x - mean) * (x - mean)).reduce(_ + _)
    sqrdDevFromMean
  }

}




