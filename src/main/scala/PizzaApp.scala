package com.example

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._


object PizzaApp {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    if (args.length != 1) {
      println("Specify the path to the File")
      System.exit(1)
    }


    val spark = SparkSession.builder()
      .appName("Pizza App")
      .getOrCreate()

    val pizzaDF = spark.read
      .option("inferSchema", "true")
      .option("header", "true")
      .csv(args(0))

    /*order_type
    orders_total
    address_id
    orders_cnt */

    def calculateOrdersTotalPerType(df: DataFrame): DataFrame = {
      df.select(col("order_type"), col("order_id"), col("address_id"))
        .withColumn("orders_total_per_type", count("order_id").over(Window.partitionBy("order_type")))
        .withColumn("max_order_size", max("orders_total_per_type").over(Window.partitionBy(lit(1))))
        .withColumn("orders_total_per_address", count("order_id").over(Window.partitionBy("order_type", "address_id")))
        .select(col("order_type"), col("address_id"), col("orders_total_per_type"), col("orders_total_per_address"))
        .distinct
    }

    val ordersCntDF=pizzaDF.transform(calculateOrdersTotalPerType)

    def calculateMaxOrdersCntDF(df: DataFrame): DataFrame = {
      df
        .select(
          col("order_type") as "order_type_maxi",
          last(col("orders_total_per_address")).over(Window.partitionBy("order_type").orderBy("orders_total_per_address").rangeBetween(Window.currentRow, Window.unboundedFollowing)) as "orders_cnt",
          last(col("address_id")).over(Window.partitionBy("order_type").orderBy("orders_total_per_address").rangeBetween(Window.currentRow, Window.unboundedFollowing)) as "address_id_maxi"
        ).distinct
    }

    val maxOrdersCntDF: DataFrame=ordersCntDF.transform(calculateMaxOrdersCntDF)

    def selAfterJoin(df: DataFrame): DataFrame= {
      val cols = List(col("order_type_maxi"), col("orders_total_per_type"), col("address_id_maxi"), col("orders_cnt"))
      df.select(cols:_*)
    }
    def renameColumns(df: DataFrame): DataFrame = {
      df.withColumnRenamed("order_type_maxi", "order_type")
        .withColumnRenamed("orders_total_per_type", "orders_total")
        .withColumnRenamed("address_id_maxi", "address_id")
    }

    def customJoin(df1: DataFrame)(df2: DataFrame): DataFrame = {
      df1.join(df2,
        df1.col("order_type") === df2.col("order_type_maxi") &&
          df1("address_id") === df2("address_id_maxi")
      )
    }

    val preJ=customJoin(ordersCntDF)(_)
    val joinedDF = maxOrdersCntDF.transform(preJ)
      .transform(selAfterJoin)
      .transform(renameColumns)

    joinedDF.show()

    joinedDF.write.option("header", "true").csv("porders6.csv") // ./spark-data/

    println("data successfully added to csv")


  }

}

