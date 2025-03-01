

package com.example
package advancedScalaStepik

import com.example.advancedScalaStepik.BridgePattern.Weapon
import org.apache.hadoop.shaded.org.eclipse.jetty.webapp.WebAppContext

object BridgePattern extends App {


  /*цель паттерна в том, чтобы отделить абстракцию от ее реализации и сделать так, чтобы пользователь работал только
  с абстракцией*/


  // implementor hierarchy
  trait Weapon {

    def get(): String

  }

  trait Hammer extends Weapon {
    override def get(): String = "get a hammer to attack"
  }

  trait Katana extends Weapon {
    override def get(): String = "get a katana to attack"
  }

  // abstraction hierarchy

  abstract class Player {
      self: Weapon =>
      def attack() : Unit

  }

  class SpaceMarine extends Player {
    self: Weapon =>
    override def attack(): Unit = {
      println(s"${get()}")
      println(s"attack")
    }
  }


  class Necrone(health: Int) extends Player {
    self: Weapon =>
    override def attack(): Unit = {
      println(s"current health points: ${health}")
      println(s"${get()}")
      println(s"attack")

    }
  }

    val spaceMarine1 = new SpaceMarine with Katana {}
    spaceMarine1.attack()

    val necrone1 = new Necrone(70) with Hammer {}
    necrone1.attack()



}
