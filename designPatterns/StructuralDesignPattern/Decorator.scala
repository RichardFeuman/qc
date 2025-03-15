
package com.example
package advancedScalaStepik

object Decorator extends App {

  /*
  Основные компоненты шаблона Декоратор:
Компонент (Component):

Это интерфейс или абстрактный класс, который определяет общий контракт для всех объектов, включая как базовые, так и декорированные.

В Scala это может быть трейт (trait) или абстрактный класс.

Конкретный компонент (Concrete Component):

Это класс, который реализует интерфейс компонента и представляет собой базовый объект, который может быть декорирован.

Декоратор (Decorator):

Это класс, который также реализует интерфейс компонента и содержит ссылку на объект компонента (базовый или другой декоратор).

Декоратор добавляет новое поведение до или после вызова методов базового компонента.

Конкретный декоратор (Concrete Decorator):

Это класс, который расширяет декоратор и добавляет конкретную функциональность.
   */

  import scala.util.Random

  // компонент
  trait Player {

    def playTrack(trackName: String): Unit

  }

  // Конкретный компонент

  class SimplePlayer extends Player {
    override def playTrack(trackName: String): Unit = println(s"playing track $trackName")
  }

  // декоратор

  abstract class PlayerDecorator(player: Player) extends Player {

    override def playTrack(trackName: String): Unit = player.playTrack(trackName)
  }

  // concrete decorator 1

//  class PlayerWithTrackSelection(player: Player) extends Player {
//    private val tracks = Seq("Воля и Разум", "Колизей", "Вавилон")
//    override def playTrack(trackName: String): Unit = {
//      val selectedTrackIndex = tracks.indexOf(trackName)
//      val selectedTrack = tracks(selectedTrackIndex)
//      player.playTrack(selectedTrack)
//    }
//  }

  class PlayerWithTrackSelection(player: Player) extends Player {
    private val tracks = Seq("Воля и Разум", "Колизей", "Вавилон")
    override def playTrack(trackName: String): Unit = {
      val selectedTrack = tracks.find(track => track == trackName).getOrElse("track not found")

      player.playTrack(selectedTrack)
    }
  }

  // concrete decorator 2


  class PlayerWithSingerInfoDisplaying(player: Player) extends Player {
    private val tracksMap = Map("Воля и Разум"->"Ария", "Колизей" -> "Ария", "Вавилон" -> "Ария")
    override def playTrack(trackName: String): Unit = {
      val singer = tracksMap.getOrElse(trackName, "track not found")
      singer match {
        case x if x != "track not found" => println(s"you choose track $trackName of singer $singer")
        case x if x == "track not found" => println("track not found")

      }
      player.playTrack(trackName)
    }
  }

  // usage

  val player1 = new PlayerWithTrackSelection(new SimplePlayer)
  player1.playTrack("Вавилон")

  val player2 = new PlayerWithSingerInfoDisplaying(new SimplePlayer)
  player2.playTrack("Колизей")
  
}
