/*import org.apache.spark.sql.SparkSession

object AST extends App {

  import spark.implicits._
  val spark = SparkSession.builder()
    .appName("AST")
    .config("spark.master", "local[*]")
    .getOrCreate()

  val df4 = spark.read.options(Map("delimiter"->";", "header"->"true")).csv("src/main/resources/chipotle_stores.csv")

  // Birmingham
  val df5=df4.filter($"location" === "Birmingham")
    .sort($"address".desc)
    .explain(extended = true)
}
*/