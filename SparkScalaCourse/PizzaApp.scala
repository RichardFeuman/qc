
package com.example

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.expressions.{Window, WindowSpec}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame, SparkSession}


object PizzaApp {

  private val logger = Logger.getLogger("PizzaApp")

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    if (args.length != 2) {
      //println("Specify the path to the File")
      logger.error("Specify the path to the File and out path")
      System.exit(1)
    }


    val spark = SparkSession.builder()
      .appName("new Pizza App")
      //.master("local")
      .getOrCreate()

    val pizzaDF = spark.read
      .option("inferSchema", "true")
      .option("header", "true")
      .csv(args(0))
    //.csv("src/main/resources/pizza_orders.csv")

    /*
Ваша задача - для каждого типа заказа order_type  рассчитать следующие значения:

общее количество заказов (должно быть отображено в колонке orders_total)
адреса, с которых сделано наибольшее число заказов. Найденные адреса с максимальным числом
заказов следует разместить в колонке address_id, а число заказов, сделанных с этих адресов,
отобразить в колонке orders_cnt
Итоговый датасет должен состоять из колонок order_type, orders_total, address_id, orders_cnt
 */

    val perTypeWindow = Window.partitionBy("order_type")

    val perTypeAndAddressWin = Window.partitionBy("order_type", "address_id")

    def calculateOrdersTotalPerTypeAndAddress(perTypeWin: WindowSpec)
                                             (perTypeAndAddressWin: WindowSpec)
                                             (df: DataFrame) = {
      require(perTypeWin != null, "perTypeWin cannot be null (calculateOrdersTotalPerTypeAndAddress)")
      require(perTypeAndAddressWin != null, "perTypeAndAddressWin cannot be null (calculateOrdersTotalPerTypeAndAddress)")
      require(!df.isEmpty, "df cannot be empty (calculateOrdersTotalPerTypeAndAddress)")
      val requiredColumns = Set("address_id", "order_type", "order_id")
      val missingColumns = requiredColumns.filter(col => !df.columns.contains(col))
      if (missingColumns.nonEmpty) {
        logger.error(s"DataFrame is missing required columns: ${missingColumns.mkString(", ")}")
      }
      require(missingColumns.isEmpty, s"DataFrame is missing required columns: ${missingColumns.mkString(", ")}")
      df.
        select(
          col("address_id"),
          col("order_type"),
          count("order_id")
            .over(perTypeWin) as "orders_total",
          count(col("order_id"))
            .over(perTypeAndAddressWin) as "orders_cnt"

        ).distinct()
    }

    def rankAddressesByOrders(perTypeWin: WindowSpec)(df: DataFrame) = {
      require(perTypeWin != null, "perTypeWin cannot be null (rankAddressesByOrders)")
      require(!df.isEmpty, "df cannot be empty (rankAddressesByOrders)")
      val requiredColumns = Set("address_id", "order_type", "orders_total", "orders_cnt")
      val missingColumns = requiredColumns.filter(col => !df.columns.contains(col))
      if (missingColumns.nonEmpty) {
        logger.error(s"DataFrame is missing required columns: ${missingColumns.mkString(", ")}")
      }
      require(missingColumns.isEmpty, s"DataFrame is missing required columns: ${missingColumns.mkString(", ")}")
      df.select(
        col("address_id"),
        col("order_type"),
        col("orders_total"),
        col("orders_cnt"),
        dense_rank()
          .over(
            perTypeWin
              .orderBy(
                desc("orders_cnt")
              )
          ) as "dense_rank_address_by_orders"
      ).distinct()
    }

    def getTopAddressesByOrders(df: DataFrame) = {
      require(!df.isEmpty, "df cannot be empty (getTopAddressesByOrders)")
      val requiredColumns = Set("address_id", "order_type", "orders_total", "dense_rank_address_by_orders")
      val missingColumns = requiredColumns.filter(col => !df.columns.contains(col))
      if (missingColumns.nonEmpty) {
        logger.error(s"DataFrame is missing required columns: ${missingColumns.mkString(", ")}")
      }
      require(missingColumns.isEmpty, s"DataFrame is missing required columns: ${missingColumns.mkString(", ")}")
      df.
        select(
          col("address_id"),
          col("order_type"),
          col("orders_total"),
          col("orders_cnt"),
          col("dense_rank_address_by_orders")
        )
        .filter(col("dense_rank_address_by_orders") === 1)
    }

    def extractColumns(cols: Seq[Column])(df: DataFrame): DataFrame = {
      require(!df.isEmpty, "df cannot be empty (extractColumns)")
      require(cols.size > 0, "list of cols to extract is empty")

      df.select(cols: _*)
    }

    val colsToIncludeIntoStatSeq = Seq(

      col("order_type"),
      col("address_id"),
      col("orders_total"),
      col("orders_cnt")
    )

    val initialExtractColsSeq = Seq(
      col("address_id"),
      col("order_type"),
      col("order_id")
    )

    val pizzaOrdersStatDF = pizzaDF
      .transform(extractColumns(initialExtractColsSeq))
      .transform(calculateOrdersTotalPerTypeAndAddress(perTypeWindow)(perTypeAndAddressWin))
      .transform(rankAddressesByOrders(perTypeWindow))
      .transform(getTopAddressesByOrders)
      .transform(extractColumns(colsToIncludeIntoStatSeq))

    pizzaOrdersStatDF.show(34)

    pizzaOrdersStatDF.write
      .option("header", "true")
      .option("delimiter", ",")
      .mode("overwrite")
      .csv(args(1))


  }
}
