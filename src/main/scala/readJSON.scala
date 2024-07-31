import NullFiller.spark

class readJSON extends App {
  val df = spark.read
    .format("json")
    .option("inferSchema","true")
    .load("filePath")

}
