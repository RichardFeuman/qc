import org.apache.commons.math3.ml.clustering.Cluster

object companions extends App {
  /*
  Компаньон объект в Scala - это объект, который объявлен в том же файле, что и класс, и имеет то же имя,
  что и класс. Например,
  когда следующий код сохранен в файле с именем Pizza.scala, объект Pizza считается компаньон объектом класса Pizza:
   */

  // class + object = companions
  class Starship(`type`: String, capacity: Int) {

    def doWarp(): Unit = println(s"warping to hidden galaxy ${Starship.defaultHiddenGalaxy}")

    def showShipInfo: Unit = println(s"ship info: type ${`type`}, capacity $capacity")
  }

  object Starship {

    private val defaultHiddenGalaxy: String ="Tatuin"

  }

  // instance-independent = "static

  // статические методы и свойства - это те, которые принадлежат классу в целом
  // а не конкретному его экземпляру

  // companion objects are for static fields/methods
  val ship1 = new Starship("predator", 50000)
  ship1.showShipInfo


  // singleton=one instance of a type is present

  object Cluster {
    val MAX_NODES = 20
    def getNumberOfNodes():  Int ={ 48 }

  }

  val maxNodes = Cluster.MAX_NODES


  // первичные и вторичные конструкторы в scala
  /*
  В Scala вторичные конструкторы являются дополнительными конструкторами, которые позволяют инициализировать
  объекты класса с другими параметрами, чем основной конструктор. Они имеют синтаксис def this(...) { ... }
  и должны вызывать либо другой вторичный конструктор внутри класса, либо основной конструктор */


  val defaultInch=12

  val defaultName="doublePepperone"

class Pizza(name: String, inches: Float) {

  // вторичный конструктор 1 (в случае, если не указано имя)
  def this(inches: Float) {
      this(defaultName, inches)

  }

  // в случае, если не укзано ни имя не размер
  def this() {
    this(defaultName, defaultInch)
  }
  // в случае, если не указан размер
  def this(name: String) {
    this(name, defaultInch)
  }

  override def toString = s"A $inches inch $name pizza "


}

  val defaultPizza=new Pizza()

  println(defaultPizza.toString)

  val romPizza=new Pizza("Rom pizza", 15)

  println(romPizza.toString)



}
