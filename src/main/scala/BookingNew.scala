package com.example

import org.apache.spark.sql.functions.{broadcast, col}
import org.apache.spark.sql.{Column, DataFrame, SparkSession}

object BookingNew  extends App {

    val spark = SparkSession.builder()
      .appName("booking")
      .master("local")
      .getOrCreate()

    spark.conf.set("spark.sql.autoBroadcastJoinThreshold", -1) // default = 10 mb
    // если больше, то sortMergeJoin

    def optiCustJoin(ccdf: DataFrame)(hdf: DataFrame): DataFrame = {
      hdf.join(broadcast(ccdf),
        hdf.col("is_canceled") === ccdf.col("id")
      ).distinct()
    }

    def custJoin(ccdf: DataFrame)(hdf: DataFrame): DataFrame = {
      hdf.join(ccdf,
        hdf.col("is_canceled") === ccdf.col("id")
      ).distinct()
    }

    val hbDF = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("src/main/resources/hotel_bookings.csv")

    val ccDF = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("src/main/resources/cancel_codes.csv")

    def WithFilter(filterCondition: Column)(jdf: DataFrame): DataFrame = {

      val cntDF = jdf.filter(filterCondition)
      cntDF

    }

    // для исключения из подсчета тех строк, в которых не указан reservation_status
    val filterCond1 = col("reservation_status") =!= "No-Show"

    // условие для получения расхождений
    val filterCond2 = (ccDF.col("is_cancelled") === "no" && hbDF.col("reservation_status") === "Canceled") || (ccDF.col("is_cancelled") === "yes" && hbDF.col("reservation_status") === "Check-Out")

    val filtNoShow = WithFilter(filterCond2)(_)

    val preFiltBookDiffDF = WithFilter(filterCond2)(_)

    //с broadcast

    val preJ1 = optiCustJoin(ccDF)(_)

    val filtOptiJoinedDF = hbDF.transform(preJ1)
      .transform(filtNoShow)
      .transform(preFiltBookDiffDF)

    println(filtOptiJoinedDF.count())

    filtOptiJoinedDF.explain(mode = "cost")

    // без broadcast

    /*val preJ2 = custJoin(ccDF)(_)

    val joinedDF = hbDF.transform(preJ2)

    val filtJoinedDF = joinedDF
      .transform(filtNoShow)
      .transform(preFiltBookDiffDF)

    println(filtJoinedDF.count())

    filtJoinedDF.explain(mode = "cost") */

    System.in.read()

    spark.stop()

}
