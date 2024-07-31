import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{Dataset, Encoders, SparkSession}

object datasets extends App {

  // В Apache Spark, Encoders - это механизм, который позволяет преобразовывать JVM-объекты
  // во внутренние структуры данных Spark и обратно. Encoders.product представляет собой Encoders,
  // предназначенные для работы с типами "product".
  // В Scala, "product" - это тип, который состоит из фиксированного числа упорядоченных полей

  val spark = SparkSession.builder()
    .appName("datasets_train")
    .master("local")
    .getOrCreate()

  // id,price,brand,type,mileage,color,date_of_purchase
  //0,6300,toyota,cruiser,274117.0,black,2008 12 02

  /*
  Метод product в Spark Scala используется для создания энкодера для произвольного класса (case class),
  который представляет структуру данных с фиксированным набором полей. Этот метод генерирует энкодер,
  который позволяет преобразовывать объекты данного класса во внутренний формат Spark SQL и обратно.
   */

  case class Car(
                  id: Int,
                  price: Int,
                  brand: String,
                  `type`: String,
                  mileage: Option[Double],
                  color: String,
                  date_of_purchase: String
                )

  val carsSchema=Encoders.product[Car].schema

  implicit val carsEnc1=Encoders.product[Car]

  val carsDS=spark.read.option("headers", "true")
    .schema(carsSchema)
    .option("delimiter", ",")
    .csv("src/main/resources/cars.csv").as[Car]

  // joinWith creates a Dataset with two columns _1 and _2 that each contain records for which condition holds.

  /*
  Left Semi Join (Левое полуобъединение):
  Возвращает только строки из левого DataSet'а, которые имеют совпадения со строками из правого DataSet'а.
   */

  val cheepCarsWithNotNulMil: Dataset[Car] = carsDS.filter(col("price") <= 25000).filter(col("mileage").isNotNull)

  println(cheepCarsWithNotNulMil.count())

  val leftSemiDs= {
    carsDS.joinWith(cheepCarsWithNotNulMil, carsDS("id")===cheepCarsWithNotNulMil("id"), "right_outer")
  }
  leftSemiDs.show()

}
