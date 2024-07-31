import org.apache.spark.sql
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, lit}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.functions.{col,length}

object ClusterTasks extends App {

  val spark = SparkSession.builder()
    .appName("clust")
    .config("spark.master", "local[*]")
    .getOrCreate()

  val sc = spark.sparkContext

  val arange=(1 to 100000).toArray

  // Parallelize the local collection to create an RDD
  val rdd = sc.parallelize(arange)

  println(rdd.getNumPartitions)

  println(rdd.sum())
  // Seq(rdd.fold(0)((a, b) => a + b))
  // spark.createDataFrame()


  /*wordCounts = wordPairs.reduceByKey(lambda x,y: x+y)

  wordCountsCollected = (wordsRDD
    .map(lambda x: (x,1))
    .reduceByKey(add)
    .collect()) */

  import spark.implicits._

  val epmDF=spark.read.option("delimiter", ",").option("header", "true").csv("/opt/spark-data/employee.csv")


  // name,birthday,date_of_birth

  /*
  val nameCountDF=epmDF.select($"name").withColumn("length", length(col("name"))) //.rdd.map(name => (name.getString(0), name.length))

  val reducedRDD = nameCountRDD.reduceByKey((x, y) => x+y)

  reducedRDD.collect.foreach(println)


  // from 1107

  val schema = new StructType()
    .add(StructField("name", StringType, false))
    .add(StructField("length", IntegerType, false))


  nameCountDF.show()


  // pizza orders
  */


}
