object functions extends App {

  object StarShip{

    // функция-метод
    def landing(place: String, reserved_time: Int): String= {
      val landingMsg = s"our ship will land at $place for $reserved_time"
      landingMsg
    }
    val batteryRest: (Int, Double) => Double = (hoursInFly, TotalPower) => TotalPower - 120 * hoursInFly

    }
  println(StarShip.landing("Millenium landing", 5))
  val func: (Int, Double)=>Double=StarShip.batteryRest
  val hours=Seq((5,4000.0),(10,4000.0),(15,4000.0),(20,4000.0),(25,4000.0))
  //val calcBatteryRests=hours.fold(0)(func)
  trait F{
    def func1: PartialFunction[(Int, Int), Int]

  }

  class Rr extends F {
    def func1: PartialFunction[(Int, Int), Int]= {
    new PartialFunction[(Int, Int), Int] {
      def apply(v1: (Int, Int)): Int =v1._1/v1._2

      override def isDefinedAt(x: (Int, Int)): Boolean = if (x._2!=0) {true} else {false}

    }
    // частично определенная функция с помощью синтаксического сахара
    }



  }

  /*class Sa extends F{
    def func2: PartialFunction[(Int, Int), Int]= {
      case (p,q) if q!=0 => p/q
    }
  }*/



  //val y: F= ???

  //val r: Sa= ???
  //println(y.func1.isDefinedAt(10,2))

  //println(r.func2.isDefinedAt(10,5))

  // стрелочка по сути есть реализация функции

  trait Rust{
    def vit(x:Double): Double
  }

  val bankomat:Rust=summa=>(summa)*1.1
  println(bankomat.vit(590))



  }



