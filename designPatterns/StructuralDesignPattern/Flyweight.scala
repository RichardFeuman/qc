


package com.example
package advancedScalaStepik
import scala.collection.mutable.Map

object Flyweight extends App {

  // flyweight: pen

  trait Booking {

    def book(personName: String, from: String, to: String, numPeople: Int) : Unit

  }

  // concrete flyweight

  class AirBooking(personName: String) extends Booking {
    println(s"make booking for ${personName}")
    override def book(personName: String, from: String, to: String, numPeople: Int) = {
        println(s"book seats at flyight from ${from} to ${to} for ${numPeople} people")
    }

  }

  // flyweight factory

  object AirBookingFactory {

    private val airBoorings: collection.mutable.Map[String, AirBooking]  = collection.mutable.Map()
    def getBooking(personName: String): Booking = {
      airBoorings.getOrElseUpdate(personName, new AirBooking(personName))
    }

  }

  // client code: air booking app

  class AirBoookingService {

    def bookSeat(personName: String, from: String, to: String, numPeople: Int) = {

        val booking = AirBookingFactory.getBooking(personName)
        booking.book(personName: String, from: String, to: String, numPeople: Int)
    }

  }

  // example of usage

  val app = new AirBoookingService
  app.bookSeat("batman", "Los Angeles", "Berlin", 1) // если посмотреть на результат выполнения то можно увидеть, что экземпляр для batman создается только один раз
  app.bookSeat("batman", "Acapulco", "Sidney", 1)
  app.bookSeat("joker", "Los Angeles", "Berlin", 1)

}
