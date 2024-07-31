import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import java.text.SimpleDateFormat
import java.util.Locale
import scala.io.Source

object avocadoProcessing extends App {

  val spark = SparkSession.builder()
    .appName("avocados_processing")
    .master("local")
    .getOrCreate()

  // id	Date	AveragePrice	TotalVolume	4046	4225	4770	Total Bags	Small Bags	Large Bags	XLarge Bags	type	year	region
  case class Avocado(
                      id: Int,
                      date: String,
                      avgPrice: Double,
                      volume: Double,
                      year: String,
                      region: String
                    )



  // для работы с rdd нужен конкекст
  val sc = spark.sparkContext


  def readAvocados(filename: String) =
    Source.fromFile(filename)
      .getLines()
      //удаляем первую строчку, тк в ней содержатся названия колонок
      .drop(1)
      // данные в колонках разделяются запятой
      .map(line => line.split(","))
      // построчно считываем данные в case класс
      .map(values => Avocado(
        values(0).toInt,
        values(1),
        values(2).toDouble,
        values(3).toDouble,
        values(12),
        values(13))
      ).toList


  def toDate(date: String, dateFormat: String): Option[Long] = {
    // для преобразования строки даты в миллисекунды
    val format = new SimpleDateFormat(dateFormat, Locale.ENGLISH)
    Option(format.parse(date).getTime) // milliseconds
  }

  val avocadoRDD:RDD[Avocado]=sc.parallelize(readAvocados("src/main/resources/avocado.csv"))

  //подсчитайте количество уникальных регионов (region), для которых представлена статистика
  val cntDistRegs=avocadoRDD.filter(rec=> rec.date.nonEmpty).keyBy(rec=>rec.region).reduceByKey((reg, _)=>reg).values.count()
  println(cntDistRegs)

  //выберите и отобразите на экране все записи о продажах авокадо, сделанные после 2018-02-11
  val afterFeb2018RDD=avocadoRDD.filter(rec=> rec.date.nonEmpty && toDate(rec.date, "yyyy-MM-dd").getOrElse(0L)>toDate("2018-02-11", "yyyy-MM-dd").getOrElse(0L))
  afterFeb2018RDD.collect().toList.foreach(println)

  //найдите месяц, который чаще всего представлен в статистике
  val mostFreqMonthRDD=avocadoRDD.filter(rec=>rec.date.nonEmpty).groupBy(rec=>rec.date.substring(5,7)).sortBy(rec=>rec._2.toList.length, ascending = false)
  println(mostFreqMonthRDD.collect()(0)._1)

  //найдите максимальное и минимальное значение avgPrice
  implicit val avgPriOrdering: Ordering[Avocado] =
    Ordering.fromLessThan[Avocado]((a1: Avocado , a2: Avocado) => a1.avgPrice < a2.avgPrice)

  val maxAvgPri=avocadoRDD.max().avgPrice

  val minAvgPri=avocadoRDD.min().avgPrice

  println(s"maxi avgPrice $maxAvgPri")

  println(s"mini avgPrice $minAvgPri")

  //отобразите средний объем продаж (volume) для каждого региона (region)
  val avocadoCuttedRDD = avocadoRDD.map(r=>(r.region, r.volume))

  val initialSalesCount = (0.0, 0)


  val aggregateSalesRDD=avocadoCuttedRDD.aggregateByKey(initialSalesCount)(
    (salesCount, saleAmount) => (salesCount._1 + saleAmount, salesCount._2 + 1),
    (salesCount1, salesCount2) => (salesCount1._1 + salesCount2._1, salesCount1._2 + salesCount2._2)
  )
  val averageSalesRDD = aggregateSalesRDD.map{case (reg,(salesAm, salesCnt)) => (reg, salesAm/ salesCnt)}

  averageSalesRDD.collect().foreach(println)

  /*val regAvgVolRDD = avocadoCuttedRDD.mapValues(volume => (volume, 1))
    .reduceByKey((acc, newSales) => (acc._1 + newSales._1, acc._2 + newSales._2))

    //.mapValues{ case (total, count) => total / count }

    //.map(x => (x._1, x._2))

  regAvgVolRDD.collect().toList.foreach(println) */

  val nums = sc.parallelize(Array(1, 3))

  val result = nums.map(x => x* x).flatMap(x => Range(1, x, 1)).filter(x => x % 3 ==0)
  println(result.collect())

}
