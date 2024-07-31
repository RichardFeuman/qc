import auto2cleanDS.spark
import org.apache.spark.sql.{DataFrame, Dataset, Encoders, SparkSession}
import org.apache.spark.sql.functions.{col, udf}

import scala.reflect.internal.util.Position

object hrFinder extends App {

  val spark = SparkSession.builder()
    .appName("hr_finder")
    .master("local")
    .getOrCreate()

  import spark.implicits._


  val posList=Iterator.continually(scala.io.StdIn.readLine()).takeWhile(_.nonEmpty).toList
  println(posList)

  case class Position(
                       PositionID: String,
                       Position: String
                      )

  implicit val encoder=Encoders.product[Position]

  /*def filterPos(ds: Dataset[Position]): Dataset[Position] = {
    // PositionID,Position
    val posToScan = posList
    ds.map(r=>Position(r.PositionID, r.Position.toLowerCase()))
      .filter(p=>posToScan.exists(e => p.Position.indexOf(e.toLowerCase()) != -1))

  } */

  def filterPos(ds: Dataset[Position]): Dataset[Position] = {
    // PositionID,Position
    val posToScan = posList
    ds.filter(p=>posToScan.exists(e => p.Position.toLowerCase.indexOf(e.toLowerCase()) != -1))

  }

  val hrDS = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv("src/main/resources/hrdataset.csv")
    .select(col("PositionID"), col("Position")).as[Position]

  hrDS.show()
    //.as[Position]

  val scannedHRDs=hrDS.transform(filterPos)
  scannedHRDs.show(45)

}
