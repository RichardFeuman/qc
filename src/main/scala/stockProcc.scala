import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{rank, row_number, year}

object stockProcc extends App {

  val spark = SparkSession.builder()
    .appName("stocks")
    .master("local")
    .getOrCreate()

  import spark.implicits._

  val stockData=spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv("src/main/resources/UBER.csv")

  val window=Window.partitionBy(year($"date").as("year")).orderBy($"Close")

  stockData.withColumn("rank", row_number() over window)
    .filter($"rank"===1)
    .sort($"Close".desc)
    .show()

}
