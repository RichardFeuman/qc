//import NullFiller.spark
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{coalesce, col, lit}
//import spark.implicits._


object NullFiller extends App {

  val spark = SparkSession.builder()
    .appName("athletic_shoes")
    .master("local")
    .getOrCreate()

  val athlShoesDF= spark.read
    .option("header", "true")
    .csv("src/main/resources/athletic_shoes.csv")

  val athlShoes2DF=athlShoesDF.na.drop(Seq("item_name", "item_category"))


  val athlShoes3DF=athlShoes2DF
  .select(
      col("item_category") as "item_category"
    , col("item_name") as "item_name"
    , coalesce(col("item_after_discount"), col("item_price")) as "item_after_discount"
    , col("item_price") as "item_price"
    , coalesce(col("percentage_solds"), lit(-1).cast("tinyint")) as "percentage_solds"
    , coalesce(col("item_rating"), lit(0).cast("tinyint")) as "item_rating"
    , coalesce(col("item_shipping"), lit("n/a").cast("String")) as "item_shipping"
    , coalesce(col("buyer_gender"), lit("unknown").cast("String")) as "buyer_gender"
  ).na.fill(Map("item_price"->"n/a"))

  case class Shoes(
                    item_category: String,
                    item_name: String,
                    item_after_discount: String,
                    item_price: String,
                    percentage_solds: Int,
                    item_rating: Int,
                    item_shipping: String,
                    buyer_gender: String
                  )

  //val athlShoesDS=athlShoes3DF.as[Shoes]
  // athlShoesDS.show(5)
}
