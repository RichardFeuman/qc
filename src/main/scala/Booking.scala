package com.example


import org.apache.spark.sql.catalyst.dsl.expressions.StringToAttributeConversionHelper
import org.apache.spark.sql.{Column, DataFrame, SparkSession}
import org.apache.spark.sql.functions.{broadcast, col, when}

object Booking extends App {


  val spark = SparkSession.builder()
    .appName("booking")
    .master("local")
    .getOrCreate()

  /*val sc=spark.sparkContext

  /* val bookingRDD = sc.textFile(s"src/main/resources/hotel_bookings.csv")
    .map{ line =>
      val fields = line.split(",")
      (fields(1).toInt, fields(fields.size-1))
    } */

  val delimeter = ","
  val bookingRDD = sc.textFile("src/main/resources/hotel_bookings.csv")

  val header = bookingRDD.first()
  val bookingRDDWoHeader = bookingRDD.filter(_(0) != header(0))

  val bookingRDD2WoHeader = bookingRDDWoHeader
    .map {
    rec =>
      val fields = rec.split(",")
      (fields(1).toInt, fields(30))
  } //.collect().foreach(println)

  val ccodesRDD = sc.textFile("src/main/resources/cancel_codes.csv")

  val header2 = ccodesRDD.first()
  val ccodesRDDWoHeader = ccodesRDD.filter(_(0) != header2(0))

  val ccodesRDD2WoHeader = ccodesRDDWoHeader
    .map{
      rec =>
        val fields = rec.split(",")
        (fields(0).toInt, fields(1))
    }

   */

    def sel(cols: List[Column])(df: DataFrame): DataFrame = {
    df.select(cols: _*)
  }

  // arrival_date_year,arrival_date_month,arrival_date_week_number,arrival_date_day_of_month

  /*
  No-Show относится к reservation_status; статус No-Show применим в ситуации,
  когда гость забронировал номер в отеле, но в день заезда не появился, не отменив при этом бронь.
   */

  def optiCustJoin(ccdf: DataFrame)(filtCond1: Column)(filtCond2: Column)(hdf: DataFrame): DataFrame = {
    hdf.join(broadcast(ccdf),
        hdf.col("is_canceled") === ccdf.col("id") && (filtCond1) && (filtCond2)
      ).distinct()
  }

  /*def custJoin(ccdf: DataFrame)(filtCond1: Column)(filtCond2: Column)(hdf: DataFrame): DataFrame = {
    hdf.join(ccdf,
        hdf.col("is_canceled") === ccdf.col("id") && (filtCond1) && (filtCond2)
      )
      .select(
        hdf.col("reservation_status"),
        hdf.col("is_canceled"),
        ccdf.col("is_cancelled"),
        ccdf.col("id")
      )
  } */

  def custJoin(ccdf: DataFrame)(filtCond1: Column)(filtCond2: Column)(hdf: DataFrame): DataFrame = {
    hdf.join(ccdf,
        hdf.col("is_canceled") === ccdf.col("id") && (filtCond1) && (filtCond2)
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



  /*def condi(df: DataFrame, col: Column, cond: String): DataFrame ={
    // чтобы фильтровать на основе поля is_canceled из df с cc
    df.filter(_=>col==col($"cond"))
  }*/

  def WithFilter(filterCondition: Column)(jdf: DataFrame) : DataFrame ={

    val cntDF=jdf.filter(filterCondition)
    cntDF

  }

  def dfToSeq(df: DataFrame): Seq[Int] = {
      df.collect().map(_.getInt(0)).toSeq
  }

  // для исключения из подсчета тех строк, в которых не указан reservation_status
  val filterCond1 = col("reservation_status") =!= "No-Show"

  val filterCond2 = ( ccDF.col("is_cancelled")==="no" && hbDF.col("reservation_status") === "Canceled" ) || ( ccDF.col("is_cancelled") === "yes" && hbDF.col("reservation_status") === "Check-Out" )

  // с broadcast
  val preJ1=optiCustJoin(ccDF)(filterCond1)(filterCond2)(_)

  val optiJoinedDF=hbDF.transform(preJ1)

  println(optiJoinedDF.count())

  optiJoinedDF.explain()

  //id,is_cancelled

  /*
  val notCancelledCodes = ccDF.transform(sel(List(col("id"), col("is_cancelled"))))
                              .transform(WithFilter(col("is_cancelled") === "no"))
                              .transform(sel(List(col("id"))))

  val cancelledCodes = ccDF.transform(sel(List(col("id"), col("is_cancelled"))))
    .transform(WithFilter(col("is_cancelled") === "no"))
    .transform(sel(List(col("id"))))

  //val filterCond2 = (col("is_cancelled")==="no") && col("reservation_status") === "Canceled")) || (( col("is_cancelled")==="yes") && col("reservation_status") === "Check-Out" )

  val filterCond2 = ( ccDF.col("is_cancelled") === "no" && hbDF.col("reservation_status") === "Canceled" ) || ( ccDF.col("is_cancelled") === "yes" && hbDF.col("reservation_status") === "Check-Out" )

  //val filterCond2 = ( col("is_cancelled") === "yes" && col("reservation_status") === "Check-Out" )

  val preFiltBookDiffDF=WithFilter(filterCond2)(_)

  val preFiltNoShow=WithFilter(filterCond1)(_)

  // с broadcast
  val preJ1=optiCustJoin(ccDF)(_)

  val optiJoinedDF=hbDF.transform(preJ1)

  //optiJoinedDF.show()

  val cntBookDiffDF = optiJoinedDF
                      .transform(preFiltNoShow)
                      .transform(preFiltBookDiffDF)

  cntBookDiffDF.show()
  println(cntBookDiffDF.count())

  cntBookDiffDF.explain()

  // без broadcast

  val preJ2=custJoin(ccDF)(_)

  val joinedDF=hbDF.transform(preJ2)

  val cntBookDiff2DF = joinedDF
                      .transform(preFiltNoShow)
                      .transform(preFiltBookDiffDF)

  println(cntBookDiff2DF.count())

  cntBookDiff2DF.explain()

  */
  System.in.read()

  spark.stop()






}
