
package com.example
package advancedScalaStepik

object PimpMyLibrary2 extends App {

  /*
  Данный паттерн, как адаптер и декоратор, нацелен на расширение функционала недоступной для
  изменения части кода (сторонней библиотеки). Преимуществом pimp my library является то,
  что паттерн (в отличие от того же декоратора) отлично сработает даже
  тогда, когда расширять функционал придется для класса, отмеченного final.
   */

  /*
  Сам паттерн описывается классом, который:

  отмечен, как implicit
  extends AnyVal
   */

  case class Message(text: String, from: String, isReaded: Boolean)

  object extension {

    implicit class MessageExtension(val messages: Iterable[Message])  extends AnyVal {
      def displayUnreadedMessages() ={

        messages.foreach{
          case msg => if(!msg.isReaded) println(s"you get the message from ${msg.from} with text ${msg.text}")
        }
      }
    }
  }

  // example of usage

  import extension._

  val messageQueue = Seq(Message("thank you", "Batman", false), Message("you win. Chocolate from me", "Bob", true) )

  messageQueue.displayUnreadedMessages()

}
