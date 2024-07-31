import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import java.text.SimpleDateFormat
import java.util.Locale

object auto2cleanDS extends App {


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

  case class preCarAvgMileage(

                               sum_mileage: Double,
                               cnt: Int

                             )

  case class CarYearsAndAvgMil(
                                id: Int,
                                years_sp: Long,
                                avg_mil: Double
                              )

  case class CarYearsAndAvgWoId(
                                 years_sp: Long,
                                 avg_mil: Double
                              )



  implicit val encoder: ExpressionEncoder[Car] = ExpressionEncoder[Car]

  implicit val encoder2: ExpressionEncoder[CarYears] = ExpressionEncoder[CarYears]

  implicit val encoder3: ExpressionEncoder[preCarAvgMileage] = ExpressionEncoder[preCarAvgMileage]

  implicit val encoder4: ExpressionEncoder[CarYearsAndAvgMil] = ExpressionEncoder[CarYearsAndAvgMil]

  implicit val encoder5: ExpressionEncoder[CarYearsAndAvgWoId] = ExpressionEncoder[CarYearsAndAvgWoId]

  implicit val encoder6: ExpressionEncoder[(Car, CarYearsAndAvgWoId)] = ExpressionEncoder[(Car, CarYearsAndAvgWoId)]

  val carsDF: DataFrame = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv("src/main/resources/cars.csv")

  val carsDS: Dataset[Car] = carsDF.as[Car]

  def convStrDate(StrDate: String): String ={
    // для преобразования строки-даты в конвертируемый в дату вид
    if (StrDate.split(" ").length==3 && StrDate.split(" ")(0).length==4 && StrDate.split(" ")(1).length==2 && StrDate.split(" ")(2).length==2) {
      StrDate.replaceAll(" ", "-")
    } else if (StrDate.split(" ").length==3 && StrDate.split(" ")(0).length==4 && StrDate.split(" ")(1)=="Apr" && StrDate.split(" ")(2).length==2) {
      StrDate.replaceAll("Apr", "-04-").replaceAll(" ", "")
    } else {
      StrDate
    }
  }

  def toDate(date: String, dateFormat: String): Option[Long] = {
    // для преобразования строки даты в миллисекунды
    val format = new SimpleDateFormat(dateFormat, Locale.ENGLISH)
    Option(format.parse(date).getTime) // milliseconds
  }

  def calcYearsSincePurchase(ms: Long): Long = {
    // для преобразования миллисекунд в число лет
    val diff= System.currentTimeMillis() - ms
    val seconds = Math.floor(diff / 1000)
    val minutes = Math.floor(seconds / 60)
    val hours = Math.floor(minutes / 60)
    val days = Math.floor(hours / 24)
    val years = Math.floor(days / 365)
    years.toLong

  }

  def strToDate(date: String):Option[Long]= if (toDate(date, "yyyy-MM-dd").getOrElse(0)!=0) {toDate(date, "yyyy-MM-dd")}
    // для преобразования строки в дату
  else if (toDate(date, "MM/dd/yyyy").getOrElse(0)!=0)
  {toDate(date, "MM/dd/yyyy")}
  else if (toDate(date, "yyyy MMM dd").getOrElse(0)!=0)
  {toDate(date, "yyyy MMM dd")}
  else if (toDate(date, "yyyy MM dd").getOrElse(0)!=0)
  {toDate(date, "yyyy MM dd")}
  else if (toDate(date, "yyyy MMMM dd").getOrElse(0)!=0)
  {toDate(date, "yyyy MMMM dd")}
  else None


  def getMeanMil(cars: Dataset[Car]): Double = {
    // для расчета среднего пробега
    val stats = cars
      .map(car => preCarAvgMileage(car.mileage.getOrElse(0.0), 1)).reduce((a, b) => preCarAvgMileage(a.sum_mileage + b.sum_mileage, a.cnt + b.cnt))

    val meanMil = stats.sum_mileage/stats.cnt
    meanMil
  }


  def addYearsSp(ds: Dataset[Car]): Dataset[CarYears]=ds.map(car=>CarYears(car.id, calcYearsSincePurchase(strToDate(convStrDate(car.date_of_purchase)).getOrElse(0L))))

  def addMeanMil(ds: Dataset[CarYears]): Dataset[CarYearsAndAvgMil]=ds.map(row=> CarYearsAndAvgMil(row.id, row.years_since_purchase, meanMileage))

  def rmExtraCId(jds: Dataset[(Car, CarYearsAndAvgMil)]):Dataset[(Car, CarYearsAndAvgWoId)]=jds.map(r=>
    (Car(r._1.id, r._1.price, r._1.brand, r._1.`type`, r._1.mileage, r._1.color, r._1.price.toString), CarYearsAndAvgWoId(r._2.years_sp, r._2.avg_mil) ))

  val carsYearsDs: Dataset[CarYears] = carsDS.transform(addYearsSp)

  val meanMileage=getMeanMil(carsDS)

  val yearAndMeanMilDs=carsYearsDs.transform(addMeanMil)

  val joinedDS: Dataset[(Car, CarYearsAndAvgMil)]= carsDS.joinWith(yearAndMeanMilDs, carsDS.col("id").cast("int") === yearAndMeanMilDs.col("id").cast("int"))

  val joined2DS: Dataset[(Car, CarYearsAndAvgWoId)]=joinedDS.transform(rmExtraCId)
  joined2DS.show(truncate = false)

  case class Sales(
                    customer: String,
                    product: String,
                    price: Double,
                  )





}
