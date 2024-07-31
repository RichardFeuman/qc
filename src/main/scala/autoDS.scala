import org.apache.spark.sql.{Column, DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object autoDS extends App {

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
                 mileage: Double,
                 color: String,
                 date_of_purchase: String
                )

  implicit val encoder: ExpressionEncoder[Car] = ExpressionEncoder[Car]

  case class CarWithAvgMil(
                            id: Int,
                            price: Int,
                            brand: String,
                            `type`: String,
                            mileage: Double,
                            color: String,
                            date_of_purchase: String,
                            avg_mileage: Double)

  implicit val encoder2: ExpressionEncoder[CarWithAvgMil] = ExpressionEncoder[CarWithAvgMil]

  case class CarInfo(
                      id: Int,
                      price: Int,
                      brand: String,
                      `type`: String,
                      mileage: Double,
                      color: String,
                      date_of_purchase: String,
                      avg_mileage: Double,
                      years_since_purchase: Double
                    )

  implicit val encoder3: ExpressionEncoder[CarInfo] = ExpressionEncoder[CarInfo]

  val carsDF: DataFrame = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv("src/main/resources/cars.csv")

  val carsDS: Dataset[Car] = carsDF.as[Car]

  def replaceNull(columnName: String, column: Column): Column =
    coalesce(col(columnName), column).as(columnName)

  def fillMissMil(df: Dataset[Car]):Dataset[Car] = {
      df.select(
        col("id") as "id",
        col("price") as "price",
        col("brand") as "brand",
        col("type") as "type",
        replaceNull("mileage", lit(0)) as "mileage",
        col("color") as "color",
        col("date_of_purchase") as "date_of_purchase",
        ).as[Car]
  }

  def calcAvgMileage(ds: Dataset[Car]): Dataset[CarWithAvgMil]= {
    ds.select(
      col("id") as "id",
      col("price") as "price",
      col("brand") as "brand",
      col("type") as "type",
      col("mileage") as "mileage",
      col("color") as "color",
      col("date_of_purchase") as "date_of_purchase",
    ).withColumn("avg_mileage", avg("mileage") over Window.partitionBy(lit(1))).as[CarWithAvgMil]
  }

  def calcYearSincePurchase(ds: Dataset[CarWithAvgMil]): Dataset[CarInfo] = {
    ds.select(
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
          .otherwise("Unknown Format").as("pre_formatted_date"))
      .withColumn("years_since_purchase",
        floor(months_between(current_date(), date_format(col("pre_formatted_date"), "yyyy-MM-dd"))/lit(12)))
      .as[CarInfo]

  }

  carsDS.transform(fillMissMil).transform(calcAvgMileage)
    .transform(calcYearSincePurchase)
    .show()

}
