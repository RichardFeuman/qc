
package com.example
package advancedScalaStepik

import scala.collection.mutable

object Observer extends App {

  /*
  Субъект (Subject):

    Объект, за которым наблюдают. Он содержит список наблюдателей и методы для их добавления, удаления и уведомления.

    Пример: Observable.

  Наблюдатель (Observer):

    Объект, который подписывается на изменения субъекта. Он содержит метод для обработки уведомлений.

  Пример: Observer.

  Конкретный субъект (Concrete Subject):

  Реализация субъекта, которая хранит состояние и уведомляет наблюдателей при его изменении.

    Пример: ConcreteObservable.

  Конкретный наблюдатель (Concrete Observer):

  Реализация наблюдателя, которая определяет, как реагировать на уведомления.

  Пример: ConcreteObserver.

   */

  // observer
  trait Observer {

    def update(forecasts: mutable.Map[String, Forecast]): Unit

  }


  // concrete observer
  case class User(id: String, name: String ) extends Observer {

    override def update(forecasts: mutable.Map[String, Forecast]): Unit =
      println(name + " " + forecasts.values.map(x=> x.date + " " + x.temperature + " " + x.humidity))
  }



  case class Forecast(date: String, temperature: Double, humidity: Double)

  // subject

  trait Observable {

    def addUser(obs: User): Unit

    def notifyUser(): Unit

    def removeUser(obs: User): Unit

    def publishWeatherForecast(fc: Forecast): Unit

    def updateForecast(fc: Forecast): Unit

  }

  // concrete subject

  class WeatherForecastService extends Observable {

    private val forecasts : mutable.Map[String, Forecast] = mutable.Map()

    private val observers: mutable.Map[String, User] = mutable.Map()

    override def addUser(user: User) = {
      observers.put(user.id, user)
    }

    override def notifyUser(): Unit = {

      observers.foreach(_._2.update(forecasts))

    }

    override def updateForecast(fc: Forecast): Unit = {
      forecasts.put(fc.date, fc)
      notifyUser()
    }

    override def publishWeatherForecast(fc: Forecast): Unit = {
      forecasts.put(fc.date, fc)
      notifyUser()
    }

    override def removeUser(user: User): Unit = {
      observers.remove(user.id)
    }

  }

  val ironMam = User("1", "Iron Man")
  val captainAmerica = User("2", "Captain America")

  val forecast1 = Forecast("2025-03-25", 10, 10)

  val forecast2 = Forecast("2025-03-26", 12, 15)

  val forecastService = new WeatherForecastService
  forecastService.addUser(ironMam)
  forecastService.addUser(captainAmerica)

  forecastService.publishWeatherForecast(forecast1)
  forecastService.publishWeatherForecast(forecast2)

  forecastService.updateForecast(Forecast("2025-03-24", 13, 13))


}
