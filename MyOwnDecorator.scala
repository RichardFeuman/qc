package com.example

object MyOwnDecorator extends App {

  /*

  The Decorator pattern is a design pattern that allows you to add new behavior or features to an object
  without modifying its underlying code. In software development, this pattern enables you to extend the
  functionality of a class by creating decorator classes that wrap around the original class.
  These decorators add additional features to the object being decorated, without altering its core functionality.

  Component: This is the common interface or abstract class representing the base object that needs to be
  decorated. It defines the basic operations that decorators and the base object share.

  Concrete Component: This is the implementation of the Component interface, representing the original
  class that you want to enhance with additional features.

  Decorator: This is the abstract class that also implements the Component interface but has
  an additional instance variable to hold the reference to the Component. Decorators provide a common interface for all concrete decorators.

  Concrete Decorator: These are the classes that extend the Decorator class and add new features to the component.
  Each concrete decorator can add specific behavior before or after delegating to the wrapped component.

  We can use a coffee shop example to present the Decorator pattern, where just Coffee is
  the concrete component and Coffee with condiments are the decorators.

   */

  // my own example of decorator

  // component

  trait Pie {
    def cost(): Double
    def description(): String
  }

  // concrete component

  class SimplePie extends Pie {

    def cost(): Double = 8.5
    def description(): String = "sweet pie with strawberry"

  }

  // decorator

  abstract class PieDecorator(pie: Pie) extends Pie {

    def cost(): Double = pie.cost()
    def description() = pie.description()

  }

  // concrete decorator 1:

  class Crumble(pie: Pie) extends PieDecorator(pie) {

    override def cost() = super.cost() + 3
    override def description() = super.description() + " with chocolate crumble"

  }

  // concrete decorator 2:

  class SugarFree(pie: Pie) extends PieDecorator(pie) {

    override def cost() = super.cost() - 0.5
    override def description() = super.description() + " sugar free"

  }

  var sweetPie1: Pie = new SimplePie()

  println(s"price of ${sweetPie1.description()}: ${sweetPie1.cost()}")

  // add crumble
  sweetPie1 = new Crumble(sweetPie1)

  println(s"price of ${sweetPie1.description()}: ${sweetPie1.cost()}")

  // minus sugar

  sweetPie1 = new SugarFree(sweetPie1)

  println(s"price of ${sweetPie1.description()}: ${sweetPie1.cost()}")

}
