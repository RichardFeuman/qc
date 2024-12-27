package com.example

object MyOwnExampleOfFlyweightPattern extends App {

  /*
  The Flyweight pattern aims to minimize memory usage by sharing common data between multiple objects.
  It is useful when you have a large number of objects that have some shared intrinsic (inherent) state and some
  extrinsic (contextual) state that can be externalized.

  Simply put, the flyweight pattern is based on a factory which recycles created objects
  by storing them after creation. Each time an object is requested, the factory looks up the object in order
  to check if it’s already been created. If it has, the existing object is returned — otherwise, a new one is created, stored and then returned.

  To implement the Flyweight pattern, you typically have two main components:

  Flyweight: This is the interface or abstract class that defines the operations shared by multiple objects.
  It also declares methods for setting and getting the extrinsic state, if any.

  ConcreteFlyweight: This is the implementation of the Flyweight interface that represents the shared objects.
  Instances of this class are shared among multiple contexts.
   */

  // flyweight
  trait Droid {

    def fire(target: String, weapon: String)

  }

  // concrete flyweight (implementation)

  class FlyDroid(droid: String) extends Droid {
    println(s"create fly droid with name $droid")
    def fire(target: String, weapon: String) = println(s"$droid fire by $target using $weapon")

  }

  // droid factory

  object DroidFactory {

    private val droids: collection.mutable.Map[String, FlyDroid] = collection.mutable.Map()

    def getDroid(droidName: String) =
      droids.getOrElseUpdate(droidName, new FlyDroid(droidName))

  }

  // client code: droid app

  case class TargetCoords(x: Double, y: Double)

  class DroidApp {

    def attackTarget(targetCoords: TargetCoords, targetName: String, droid: String, veapon: String) ={

        val droid1 = DroidFactory.getDroid(droid)
        println(s"$droid prepare to fire target $targetName with coords : $targetCoords using $veapon")
        droid1.fire(targetName, veapon)
    }

  }

  // example of usage

  val app = new DroidApp()
  app.attackTarget(TargetCoords(56.55, 86.78), "bunker", "kamikadze", "rockets")
  app.attackTarget(TargetCoords(36.55, 96.75), "turel", "twilight sun", "machine gun")
  app.attackTarget(TargetCoords(56.55, 86.78), "frigate", "kamikadze", "bombs")
  app.attackTarget(TargetCoords(56.55, 86.78), "tank", "double flash", "bombs")

  /*
  create fly droid with name kamikadze
  kamikadze prepare to fire target bunker with coords : TargetCoords(56.55,86.78) using rockets
  kamikadze fire by bunker using rockets
  create fly droid with name twilight sun
  twilight sun prepare to fire target turel with coords : TargetCoords(36.55,96.75) using machine gun
  twilight sun fire by turel using machine gun
  kamikadze prepare to fire target frigate with coords : TargetCoords(56.55,86.78) using bombs
  kamikadze fire by frigate using bombs
  create fly droid with name double flash
  double flash prepare to fire target tank with coords : TargetCoords(56.55,86.78) using bombs
  double flash fire by tank using bombs
   */



}
