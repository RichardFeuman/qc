import JSONWork.amazonRDD
import io.circe._
import io.circe.generic.semiauto.deriveDecoder
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import io.circe.generic.auto._
import io.circe._, io.circe.generic.semiauto._
import scala.util.parsing.json
import scala.io.Source
import scala.util.parsing.json._

object JSONWork extends App {

  val spark = SparkSession.builder()
    .appName("work_with_json")
    .master("local")
    .getOrCreate()

  case class Amazon (
                             uniqId: Option[String],
                             productName: Option[String],
                             manufacturer: Option[String],
                             price: Option[String],
                             numberAvailable: Option[String],
                             numberOfReviews: Option[Int]
                           )

  implicit val jsonDecoder: Decoder[Amazon] = deriveDecoder[Amazon]

  val sc = spark.sparkContext

  //sc.wholeTextFiles("test.json").values().map(json.loads)

  val amazonRDD= sc.textFile("src/main/resources/amazon_products.json")

  val a=amazonRDD.map(line=>jawn.decode[Amazon](line).toOption)

  amazonRDD.map(line=>jawn.decode[Amazon](JSON.parseRaw(line).get.toString)).foreach(println)
  //a.foreach(println)
  // Преобразование JSON строк в case класс Product
  /*val resultRDD = amazonRDD.mapPartitions(iter => {
    iter.flatMap { line =>
      jawn.decode[Amazon](line).toOption
    }
  })

  print(resultRDD.collect().toList)
  resultRDD.foreach(println)*/

  val jsonStringWithMissingFields =
    """{
      | "textField" : "textContent",
      | "nestedObject" : {
      | "arrayField" : null
      | }
      |}""".stripMargin

  case class outputData(field1 : String, field2: String, field3 : String)

  /*def jsonParser(JsonDataFile : String)  = {

    val JsonData : String = Source.fromFile(JsonDataFile).getLines.mkString

    val jsonFormatData = JSON.parseFull(JsonData).map{
      case json : Amazon  =>
        jawn.decode[Amazon](json).toOption
    }

    jsonFormatData
  }*/

    /*.map(line => line.split(","))
    .filter(values => values(0) == "Alabama")
    .map(values => Store(
      values(0),
      values(1),
      values(2),
      values(3).toDouble,
      values(4).toDouble)) */

  //storesRDD2.foreach(println)




  //When the case classes are defined, we can derive a Decoder from the class and use it to parse a JSON string.
  // Note that we’ll define a Decoder of the Nested class first:

  /*
  implicit val nestedDecoder: Decoder[Nested] = deriveDecoder[Nested]


  val decoded = decode[OurJson](jsonString)


  parseResult match {
    case Left(parsingError) =>
      throw new IllegalArgumentException(s"Invalid JSON object: ${parsingError.message}")
    case Right(json) => // here we use the JSON object
  }

  //example using circe

  val customerJson = s"""{"id" : "1", "name" : "John Doe"}"""
  case class Customer(id: String, name: String)
  val customer = decode[Customer](customerJson)

   */

}
