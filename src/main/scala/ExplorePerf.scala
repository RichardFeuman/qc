package com.example

import org.apache.spark.sql.functions.{coalesce, col, desc, lit}
import org.apache.spark.sql.{Column, DataFrame, SparkSession}

object ExplorePerf extends App {

  val spark = SparkSession.builder()
    .appName("explore_perf")
    .master("local[*]")
    .getOrCreate()

  val sc = spark.sparkContext // access point to low level api

  spark.conf.set("spark.sql.autoBroadcastJoinThreshold", -1)

  def nullReplace(col: Column)(df: DataFrame): DataFrame = {

    df.withColumn(s"$col", coalesce(col, lit("foobarbaz")))

  }

  def customSortBy(col: Column)(df: DataFrame) : DataFrame = {
    df.orderBy(desc(s"$col"))
  }

  def addColumn(df: DataFrame, n: Int): DataFrame = {
    val columns = (1 to n).map(col => s"col_$col")
    columns.foldLeft(df)((df, column) => df.withColumn(column, lit("n/a")))
  }

  val data1 = (1 to 500000).map(i => (i, i * 100))
  val data2 = (1 to 10000).map(i => (i, i * 1000))

  import spark.implicits._

  val df1 = data1.toDF("id","salary").repartition(5)
  val df2 = data2.toDF("id","salary").repartition(10)

  // добавляем колонки после join

  val repartitionedById1 = df1.repartition(col("id"))
  val repartitionedById2 = df2.repartition(col("id"))

  val joinedDF2 = repartitionedById2.join(repartitionedById1, "id")

  val dfWithColumns2 = addColumn(joinedDF2, 10)

  val transformedDFWithColumns2 = dfWithColumns2
    .transform(nullReplace(col("col_1")))
    .transform(customSortBy(col("col_1")))

  transformedDFWithColumns2.show()

  transformedDFWithColumns2.explain(mode="cost")

  // добавляем колонки до join

  val repartitionedById3 = df1.repartition(col("id"))
  val repartitionedById4 = df2.repartition(col("id"))

  val dfWithColumns3 = addColumn(repartitionedById3, 10)

  val transformedDFWithColumns3 = dfWithColumns3
    .transform(nullReplace(col("col_1")))
    .transform(customSortBy(col("col_1")))

  val joinedDF3 = repartitionedById4.join(transformedDFWithColumns3, "id")

  joinedDF3.show()

  joinedDF3.explain(mode="cost")

  System.in.read()

  spark.stop()

}
