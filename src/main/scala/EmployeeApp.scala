package module5cluster


import org.apache.spark
import org.apache.spark.sql.SparkSession


import org.apache.spark.sql.SparkSession


object EmployeeApp {
 /*
  def main2(args: Array[String]): Unit = {

    if (args.length != 1) {
      println("Specify the path to the File")
      System.exit(1)
    }

    val spark = SparkSession.builder()
      .appName("Employee App")
      .getOrCreate()


    val employeesDF = spark.read
      .option("inferSchema", "true")
      .option("header", "true")
      .csv(args(0))


    employeesDF.printSchema()

    employeesDF.show()
  } */

}

