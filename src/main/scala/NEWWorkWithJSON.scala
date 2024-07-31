import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, jawn}
import org.apache.spark.sql.SparkSession

object NEWWorkWithJSON extends App {


  val spark = SparkSession.builder()
    .appName("work_with_json")
    .master("local")
    .getOrCreate()

  //@JsonCodec
  case class Amazon(
                            uniq_id: Option[String],
                            product_name: Option[String],
                            manufacturer: Option[String],
                            price: Option[String],
                            number_available: Option[String],
                            number_of_reviews: Option[Int]
                          )

  implicit val jsonDecoder: Decoder[Amazon] = deriveDecoder[Amazon]

  val sc = spark.sparkContext

  val jsonRDD = sc.textFile("src/main/resources/amazon_products.json")

  val productsRDD = jsonRDD.flatMap {
    line =>
    jawn.decode[Amazon] (line) match {
      case Right(product) => Some(product)
      case Left(error) =>
        println(s"decoding error: $error")
        None
    }
  }

  productsRDD.foreach(println)

}
