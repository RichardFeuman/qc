package com.example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.broadcast

object JOINS extends App {

  /*
  Данный способ оптимизации применяется, когда большой DF объединяется с маленьким DF. Например,
  DF состоящий из 100000000 строчек объединяется с датафреймом в 1000 строчек.
  Примером таких данных может быть DF с заказами и DF с кодами офисов, где был сделан заказ.
   */

  val spark = SparkSession.builder()
    .appName("joins")
    .master("local")
    .getOrCreate()

  val sc = spark.sparkContext

  import spark.implicits._

  val smallDF = sc.parallelize(Seq(
    (1, "a1"),
    (2, "b2"),
    (2, "c3"),
  )).toDF("id", "code")


  val bigDS = spark.range(1, 100000000)

  val joinedDF = bigDS.join(smallDF, "id")
  joinedDF.explain()

  // чтобы улучшить ситуацию, воспользуемся оптимизацией broadcast

  // import org.apache.spark.sql.functions._
  val optimizedJoin = bigDS.join(broadcast(smallDF), "id")


  /*
   Как видите, никаких Exchange. Это стало возможно благодаря тому, что теперь каждый executor располагает не только
   данными bigDS, но и smallDF (практически smallDF был скопирован и размещен на каждом executor, которому требуются
   данные smallDF, а значит, нет необходимости в перераспределении данных или shuffle)
   */
  optimizedJoin.explain()


  /*
  spark.sql.autoBroadcastJoinThreshold


   (10 MB)	Configures the maximum size in bytes for a table that will be broadcast to all
   worker nodes when performing a join. By setting this value to -1 broadcasting can be disabled.
   Note that currently statistics are only supported for Hive Metastore tables where the command
   ANALYZE TABLE <tableName>
   COMPUTE STATISTICS noscan has been run.
   */




}
