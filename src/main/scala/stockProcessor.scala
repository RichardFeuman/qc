

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{rank, year}

class stockProcessor extends App {

    val spark = SparkSession.builder()
      .appName("stocks")
      .master("local")
      .getOrCreate()

    import spark.implicits._

    val stockData=spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("src/main/resources/UBER.csv")

    /*
    Чтобы применить оконные функции, нужно инициализировать экземпляр WindowSpec.
    Делается это через методы класса Window:
    orderBy(*cols)
    partitionBy(*cols)
    rangeBetween(start, end)
    rowsBetween(start, end)
     */
    val window=Window.partitionBy(year($"date").as("year")).orderBy($"Close")

    stockData.withColumn("rank", rank() over window).show()
  }



