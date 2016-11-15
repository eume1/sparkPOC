import org.apache.hadoop.hbase.client.{HBaseAdmin, HTable, Put}
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HTableDescriptor}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext._



/**
 * Created by Admin on 9/2/2015.
 */

object TwitterYahooFinanceMovingAveragesJob  extends Serializable{
  val util = new utilityFunctions

  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      System.err.println("Usage: Master, DataSetPath")
      System.exit(1)
    }

    var sparkConf = new SparkConf()
    sparkConf.setAppName("TwitterYahooFinanceMovingAverages")
    val sc = new SparkContext(sparkConf)

    val dataSetPath = args(0)
    val inFile: RDD[String] = sc.textFile(dataSetPath) ///Is toString necesssary?
    val tokenizeLines: RDD[List[(String, String)]] = inFile.map(_.replace(" ", "").replace("\"", "")
        .split(",").toList).map(util.toTuple)

    val lineMap: RDD[Map[String, String]] = for {
      t <- tokenizeLines
    } yield t.toMap

    val lineMapToStockPriceInfoObjectRDD: RDD[stockPriceInfo] =
      lineMap.map(stockPriceInfo.extractRelevantStockData(_))

    val stockDataFilteredRDD:
    RDD[Option[stockPriceInfo]]= lineMapToStockPriceInfoObjectRDD
      .map(new stockDataFilter(_).requirementsMet).filter(_.isDefined)

    //(ticker|YearMonthDay,Iterable[Option[stockPriceInfo]])
   val dateGroupKVpair: RDD[(String, Iterable[Option[stockPriceInfo]])] =
      stockDataFilteredRDD.keyBy(x =>
        x.get.ticker + "|" + x.get.dateTime.split("@")(0).split("-")(0) //year
           + x.get.dateTime.split("@")(0).split("-")(1) + //month
           x.get.dateTime.split("@")(0).split("-")(2)).groupByKey() //.groupByKey() //.groupByKey().sortBy(_._1, false)
    dateGroupKVpair.saveAsTextFile("dateGroupKVpair2.txt")

                //(ticker, Iterable[(ticker|YearMonthDay,Iterable[Option[stockPriceInfo]])])
  val stockGroupDateGroupKVpair: RDD[(String, Iterable[(String, Iterable[Option[stockPriceInfo]])])] =
                  dateGroupKVpair.keyBy(x => x._1.split("\\|")(0)).groupByKey()
    stockGroupDateGroupKVpair.saveAsTextFile("stockGroupDateGroupKVpair.txt")

                //(ticker, Iterable[(ticker|YearMonthDay,Iterable[Option[stockPriceInfo]])])
   val intraDay: RDD[(String, Iterable[(String, Iterable[Option[stockPriceInfo]])])] =
                  stockGroupDateGroupKVpair.map(x => (x._1, x._2.take(1)))

   val _5Days: RDD[(String, Iterable[(String, Iterable[Option[stockPriceInfo]])])] =
                  stockGroupDateGroupKVpair.map(x => (x._1, x._2.take(5)))

   val _30Days: RDD[(String, Iterable[(String, Iterable[Option[stockPriceInfo]])])] =
                  stockGroupDateGroupKVpair.map(x => (x._1, x._2.take(30)))

  val _250Days: RDD[(String, Iterable[(String, Iterable[Option[stockPriceInfo]])])] =
                  stockGroupDateGroupKVpair.map(x => (x._1, x._2.take(250)))

                //Iterable[Iterable[Option[stockPriceInfo]]]
                //(APPL,(dateTime,stockListReturns))
  val intraDayStockListReturns: RDD[(String, (String, stockListReturns))] =
                  intraDay.map(x => (x._1, (x._2.map(_._1).toList.max, stockListReturns.result(x))))

  val _5DaysStockListReturns =
                  _5Days.map(x => (x._1, (x._2.map(_._1).toList.max, stockListReturns.result(x))))
  val _30DaysStockListReturns =
                  _30Days.map(x => (x._1, (x._2.map(_._1).toList.max, stockListReturns.result(x))))
  val _250DaysStockListReturns =
                  _250Days.map(x => (x._1, (x._2.map(_._1).toList.max, stockListReturns.result(x))))


  val COL_FAM: Array[Byte] = Bytes.toBytes("stockAggregateResults")
  val intraDayMean = Bytes.toBytes("intraDayMean");
  val intraDaystdev = Bytes.toBytes("intraDaystdev")
  val intraDayHigh = Bytes.toBytes("intraDayHigh");
  val intraDayLow = Bytes.toBytes("intraDayLow")
  val intraDayMeanVol = Bytes.toBytes("intraDayMeanVol");

  val _5DaysStockListReturnsMean = Bytes.toBytes("intraDayMean");
  val _5DaysStockListReturnsstdev = Bytes.toBytes("intraDaystdev")
  val _5DaysStockListReturnsHigh = Bytes.toBytes("intraDayHigh");
  val _5DaysStockListReturnsLow = Bytes.toBytes("intraDayLow")
  val _5DaysStockListReturnsMeanVol = Bytes.toBytes("intraDayMeanVol");

        val _30DaysStockListReturnsMean = Bytes.toBytes("intraDayMean");
        val _30DaysStockListReturnsstdev = Bytes.toBytes("intraDaystdev")
        val _30DaysStockListReturnsHigh = Bytes.toBytes("intraDayHigh");
        val _30DaysStockListReturnsLow = Bytes.toBytes("intraDayLow")
        val _30DaysStockListReturnsMeanVol = Bytes.toBytes("intraDayMeanVol");

        val _250DaysStockListReturnsMean = Bytes.toBytes("intraDayMean");
        val _250DaysStockListReturnstdev = Bytes.toBytes("intraDaystdev")
        val _250DaysStockListReturnsHigh = Bytes.toBytes("intraDayHigh");
        val _250DaysStockListReturnsLow = Bytes.toBytes("intraDayLow")
        val _250DaysStockListReturnsMeanVol = Bytes.toBytes("intraDayMeanVol");



       //HBase Connection
        val hConf = HBaseConfiguration.create()
        hConf.set(TableInputFormat.INPUT_TABLE, "twitterStockTable")

        val hTable = new HTable( hConf, "twitterStockTable" )


        val hbaseKey: String = intraDayStockListReturns.map(x => x._1 + "|" + x._2._1).toString


          val p = new Put(Bytes.toBytes(hbaseKey))

          p.add(COL_FAM, intraDayMean, Bytes.toBytes(intraDayStockListReturns.map(x => x._2._2.mean).toString))
          p.add(COL_FAM, intraDaystdev, Bytes.toBytes(intraDayStockListReturns.map(x => x._2._2.stdev).toString))
          p.add(COL_FAM, intraDayHigh, Bytes.toBytes(intraDayStockListReturns.map(x => x._2._2.high).toString))
          p.add(COL_FAM, intraDayLow, Bytes.toBytes(intraDayStockListReturns.map(x => x._2._2.low).toString))
          p.add(COL_FAM, intraDayMeanVol, Bytes.toBytes(intraDayStockListReturns.map(x => x._2._2.meanVol).toString))

          p.add(COL_FAM, _5DaysStockListReturnsMean, Bytes.toBytes(_5DaysStockListReturns.map(x => x._2._2.mean).toString))
          p.add(COL_FAM, _5DaysStockListReturnsstdev, Bytes.toBytes(_5DaysStockListReturns.map(x => x._2._2.stdev).toString))
          p.add(COL_FAM, _5DaysStockListReturnsHigh, Bytes.toBytes(_5DaysStockListReturns.map(x => x._2._2.high).toString))
          p.add(COL_FAM, _5DaysStockListReturnsLow, Bytes.toBytes(_5DaysStockListReturns.map(x => x._2._2.low).toString))
          p.add(COL_FAM, _5DaysStockListReturnsMeanVol, Bytes.toBytes(_5DaysStockListReturns.map(x => x._2._2.meanVol).toString))

          p.add(COL_FAM, _30DaysStockListReturnsMean, Bytes.toBytes(_30DaysStockListReturns.map(x => x._2._2.mean).toString))
          p.add(COL_FAM, _30DaysStockListReturnsstdev, Bytes.toBytes(_30DaysStockListReturns.map(x => x._2._2.stdev).toString))
          p.add(COL_FAM, _30DaysStockListReturnsHigh, Bytes.toBytes(_30DaysStockListReturns.map(x => x._2._2.high).toString))
          p.add(COL_FAM, _30DaysStockListReturnsLow, Bytes.toBytes(_30DaysStockListReturns.map(x => x._2._2.low).toString))
          p.add(COL_FAM, _30DaysStockListReturnsMeanVol, Bytes.toBytes(_30DaysStockListReturns.map(x => x._2._2.meanVol).toString))

          p.add(COL_FAM, _250DaysStockListReturnsMean, Bytes.toBytes(_250DaysStockListReturns.map(x => x._2._2.mean).toString))
          p.add(COL_FAM, _250DaysStockListReturnstdev, Bytes.toBytes(_250DaysStockListReturns.map(x => x._2._2.stdev).toString))
          p.add(COL_FAM, _250DaysStockListReturnsHigh, Bytes.toBytes(_250DaysStockListReturns.map(x => x._2._2.high).toString))
          p.add(COL_FAM, _250DaysStockListReturnsLow, Bytes.toBytes(_250DaysStockListReturns.map(x => x._2._2.low).toString))
          p.add(COL_FAM, _250DaysStockListReturnsMeanVol, Bytes.toBytes(_250DaysStockListReturns.map(x => x._2._2.meanVol).toString))

        hTable.put(p)
        hTable.flushCommits()

  }



}