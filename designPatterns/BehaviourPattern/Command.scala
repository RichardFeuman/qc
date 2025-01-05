package com.example
package designPattern

object Command extends App {

  /*

Паттерн Command (Команда) — это поведенческий шаблон проектирования, который инкапсулирует запрос в виде объекта,
позволяя параметризировать клиенты с различными запросами, выстраивать очереди запросов и поддерживать отмену операций.
Основная идея заключается в том, чтобы отделить объект, инициирующий операцию, от объекта, выполняющего её.
   */

  // Интерфейс команды

  trait Command {
    def execute(): Unit
  }

  // receiver - выполняет поступающие запросы; он знае что и как делать

  class Player(var position: Int) {
    def moveForward(): Unit = {
      println(s"Moving forward. New position: ${position + 1}")
      position += 1
    }

    def moveBackward(): Unit = {
      println(s"Moving backward. New position: ${position - 1}")
      position -= 1
    }

    def attack(): Unit = {
      println("Attacking!")
    }
  }

  // Command Objects
  // 1. Player - link to receiver
  // 2. player.moveForward() - метод , который сможет вызвать метод из receiver object (этот метод и обработает поступивший запрос)
  // 3. execute() - request - что надо сделать
  class MoveForward(player: Player) extends Command {
    override def execute(): Unit = player.moveForward()
  }

  class MoveBackward(player: Player) extends Command {
    override def execute(): Unit = player.moveBackward()
  }

  class Attack(player: Player) extends Command {
    override def execute(): Unit = player.attack()
  }

  // invoker - не знает, что и как делать. Передает тому, кто знает

  class GameController {
    private var commandHistory: List[Command] = Nil

    def storeAndExecute(command: Command): Unit = {
      command.execute()
      commandHistory = command :: commandHistory
    }

  }

  // usage

  val player = new Player(0)
  val controller = new GameController()

  controller.storeAndExecute(new MoveForward(player))
  controller.storeAndExecute(new MoveForward(player))
  controller.storeAndExecute(new Attack(player))
  controller.storeAndExecute(new MoveBackward(player))

}
