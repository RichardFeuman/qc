import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{Dataset, Encoder, Encoders, SparkSession}

object CleanHRFinder extends App {

  val spark = SparkSession.builder()
    .appName("hr_finder")
    .master("local")
    .getOrCreate()

  val posList=Iterator.continually(scala.io.StdIn.readLine()).takeWhile(_.nonEmpty).toList
  println(posList)

  case class Position(
                       PositionID: String,
                       Position: String
                     )

  implicit val encoder: Encoder[Position] =Encoders.product[Position]

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

  val scannedHRDs=hrDS.transform(filterPos)
  scannedHRDs.show(45)

}
