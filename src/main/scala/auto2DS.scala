

import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.{DataFrame, Dataset, Encoder, Encoders, SparkSession}

import java.lang
import java.text.SimpleDateFormat
import java.util.Locale


object auto2DS extends App {

  val spark = SparkSession.builder()
    .appName("cars_stats")
    .master("local")
    .getOrCreate()

  spark.sql("set spark.sql.legacy.timeParserPolicy=LEGACY")

  case class Car(
                  id: Int,
                  price: Int,
                  brand: String,
                  `type`: String,
                  mileage: Option[Double],
                  color: String,
                  date_of_purchase: String
                )

  case class CarYears(
                  id: Int,
                  years_since_purchase: Long
                )

  case class CarMillisec(
                       id: Int,
                       millisec_since_purchase: Long
                     )

  case class CarAvgMileage(
                       id: Int,
                       avg_mileage: Double
                     )

  case class preCarAvgMileage(

  sum_mileage: Double,
    cnt: Int

                          )


  case class carIdAndMilPair(

                               cid: Int,
                               mileage: Option[Double]

                             )



  case class CarInfo(
                  id: Int,
                  price: Int,
                  brand: String,
                  `type`: String,
                  mileage: Option[Double],
                  color: String,
                  date_of_purchase: String,
                  years_since_purchase: Long,
                  avg_mileage: Double
                )

  implicit val encoder: ExpressionEncoder[Car] = ExpressionEncoder[Car]

  implicit val encoder2: ExpressionEncoder[CarYears] = ExpressionEncoder[CarYears]

  implicit val encoder3: ExpressionEncoder[CarAvgMileage] = ExpressionEncoder[CarAvgMileage]

  implicit val encoder4: ExpressionEncoder[preCarAvgMileage] = ExpressionEncoder[preCarAvgMileage]

  implicit val encoder5: ExpressionEncoder[carIdAndMilPair] = ExpressionEncoder[carIdAndMilPair]

  implicit val encoder6: ExpressionEncoder[CarMillisec] = ExpressionEncoder[CarMillisec]

  implicit val encoder7: ExpressionEncoder[CarInfo] = ExpressionEncoder[CarInfo]

  val carsDF: DataFrame = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv("src/main/resources/cars.csv")

  val carsDS: Dataset[Car] = carsDF.as[Car]

  def convStrDate(StrDate: String): String ={
      if (StrDate.split(" ").length==3 && StrDate.split(" ")(0).length==4 && StrDate.split(" ")(1).length==2 && StrDate.split(" ")(2).length==2) {
        StrDate.replaceAll(" ", "-")
      } else if (StrDate.split(" ").length==3 && StrDate.split(" ")(0).length==4 && StrDate.split(" ")(1)=="Apr" && StrDate.split(" ")(2).length==2) {
        StrDate.replaceAll("Apr", "-04-").replaceAll(" ", "")
      } else {
        StrDate
      }
  }


  // id	price	brand	type	mileage	color	date_of_purchase



    /*.map(car=>Car(car.id,
      car.price,
      car.brand,
      car.`type`,
      car.mileage,
      car.color,
      car.date_of_purchase
    ))*/



  /*def toDate(date: String, dateFormat: String): Option[GregorianCalendar] = {
    //val format = new SimpleDateFormat(dateFormat, Locale.getDefault).getTimeZone("Europe/Moscow")
    //Option(format.parse(date).getTime)
    /*date.split("/").toList match {
      case yyyy :: mm :: dd :: Nil =>
        Try(
          new GregorianCalendar(
            yyyy.toInt,
            mm.toInt - 1,
            dd.toInt - 1
          )
        ).toOption
      case _ => None
    } */


  } */

  def toDate(date: String, dateFormat: String): Option[Long] = {
    val format = new SimpleDateFormat(dateFormat, Locale.ENGLISH)
    Option(format.parse(date).getTime) // milliseconds
  }

  // val testConv=carsDS.map(car=>toDate(car.date_of_purchase, "yyyy MMMM dd"))

  //testConv.show()
  /*ds.select(
    col("id") as "id",
    col("price") as "price",
    col("brand") as "brand",
    col("type") as "type",
    col("mileage") as "mileage",
    col("color") as "color",
    col("date_of_purchase") as "date_of_purchase",
    col("avg_mileage") as "avg_mileage",
    when(to_date(col("date_of_purchase"), "yyyy-MM-dd").isNotNull,
      to_date(col("date_of_purchase"), "yyyy-MM-dd"))
      .when(to_date(col("date_of_purchase"), "MM/dd/yyyy").isNotNull,
        to_date(col("date_of_purchase"), "MM/dd/yyyy"))
      .when(to_date(col("date_of_purchase"), "yyyy MMMM dd").isNotNull,
        to_date(col("date_of_purchase"), "yyyy MMMM dd"))
      .when(to_date(col("date_of_purchase"), "yyyy MMM dd").isNotNull,
        to_date(col("date_of_purchase"), "yyyy MMM dd"))
      .when(to_date(col("date_of_purchase"), "yyyy MM dd").isNotNull,
        to_date(col("date_of_purchase"), "yyyy MM dd"))
      .otherwise("Unknown Format").as("pre_formatted_date")) */

  /*def calcYearsSincePurchase(ds: Dataset[Car]): CarYears = {
    val age = (toDate("04/19/2022", "MM/dd/yyyy") - toDate(channel.created, "yyyy MMM dd")) / (1000*60*60*24)
    Age(channel.channel_name, age.toInt)
  } */

  /*

  def calcYearsSincePurchase(ds: Dataset[Car]): CarYears = {
    val years_since_purchase = ds.

      (toDate("04/19/2022", "MM/dd/yyyy") - toDate(channel.created, "yyyy MMM dd")) / (1000*60*60*24)
    Age(channel.channel_name, age.toInt)
  } */

  def calcYearsSincePurchase(ms: Long): Long = {
      val diff= System.currentTimeMillis() - ms
      val seconds = Math.floor(diff / 1000)
      val minutes = Math.floor(seconds / 60)
      val hours = Math.floor(minutes / 60)
      val days = Math.floor(hours / 24)
    val years = Math.floor(days / 365)
      years.toLong

  }

  def strToDate(date: String):Option[Long]= if (toDate(date, "yyyy-MM-dd").getOrElse(0)!=0) {toDate(date, "yyyy-MM-dd")}
  else if (toDate(date, "MM/dd/yyyy").getOrElse(0)!=0)
    {toDate(date, "MM/dd/yyyy")}
  else if (toDate(date, "yyyy MMM dd").getOrElse(0)!=0)
  {toDate(date, "yyyy MMM dd")}
  else if (toDate(date, "yyyy MM dd").getOrElse(0)!=0)
  {toDate(date, "yyyy MM dd")}
  else if (toDate(date, "yyyy MMMM dd").getOrElse(0)!=0)
  {toDate(date, "yyyy MMMM dd")}
  else None


  /*def millisToYears(millis: Long): Long ={

      val years: Long=(millis/(1000 * 60 * 60 *12).floor.toLong
      years
  }*/

  val carsYearsDs=carsDS.map(car=>CarYears(car.id, calcYearsSincePurchase(strToDate(convStrDate(car.date_of_purchase)).getOrElse(0L))))

  carsYearsDs.show()

  /*
    val pair = sc.parallelize(1 to 100)
    .map(x => (x, 1))
    .reduce((x, y) => (x._1 + y._1, x._2 + y._2))

    val mean = pair._1 / pair._2

     val infoDS: Dataset[CustomerInfo] = ordersDS
   .groupByKey(_.customerId)
   .mapGroups { (id, orders) => {
     val priceTotal = orders.map(order => order.priceEach * order.quantity).sum.round
     val ordersTotal = orders.size // будет 0, тк после вычисления priceTotal буфер пуст

     CustomerInfo(
       id,
       priceTotal
     )

   }
  }
   */
  implicit val stringEncoder: Encoder[lang.Double] = Encoders.DOUBLE

  /*def calcAvgMil(car: Dataset[Car])= {
    val pair=car.map(car=>car.mileage)
      .reduce((a,b) => (a._1 + b._1, a._2 + b._2))
  }

  def getAvgMil(car: Dataset[Car]): (Double, Int) = {
    val stats: (Double, Int) = car
      .map(car => (car.priceEach * order.quantity, order.quantity))
      .reduce((a, b) => (a._1 + b._1, a._2 + b._2))

    (stats._1, stats._2)
  }*/


  /*val getMilForAvg: Dataset[preCarAvgMileage] = {
    val pairs=carsDS.map(car => preCarAvgMileage(car.mileage.getOrElse(0.0), 1))
    pairs
      //.reduce((cm, cc) => (cm._1.getOrElse(0.0) + cm._1.getOrElse(0.0) , cc._2+cc._2))
    //CarAvgMileage(pairs._1/pairs._2)
  } */



  def getMeanMil(cars: Dataset[Car]): Double = {
    val stats = cars
      .map(car => preCarAvgMileage(car.mileage.getOrElse(0.0), 1)).reduce((a, b) => preCarAvgMileage(a.sum_mileage + b.sum_mileage, a.cnt + b.cnt))

    val meanMil = stats.sum_mileage/stats.cnt
    meanMil

  }

  case class CarYearsAndAvgMil(
                              id: Int,
                              years_sp: Long,
                              avg_mil: Double
                              )

  implicit val encoder8: ExpressionEncoder[CarYearsAndAvgMil] = ExpressionEncoder[CarYearsAndAvgMil]

  val meanMileage=getMeanMil(carsDS)
  println(meanMileage)

  val yearAndMeanMilDs=carsYearsDs.map(row=> CarYearsAndAvgMil(row.id, row.years_since_purchase, meanMileage))

  val joinedDS: Dataset[(Car, CarYearsAndAvgMil)]= carsDS.joinWith(yearAndMeanMilDs, carsDS.col("id").cast("int") === yearAndMeanMilDs.col("id").cast("int"))

  joinedDS.show(truncate = false)



  //val carsDSWith












}
