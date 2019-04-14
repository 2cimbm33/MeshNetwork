package uni.cimbulka.network.akka

import akka.actor.ActorRef
import akka.actor.ActorSystem
import uni.cimbulka.network.akka.actors.Greeter
import uni.cimbulka.network.akka.actors.Printer

fun main() {
    val system = ActorSystem.create("helloakka")

    try {
        val printerActor = system.actorOf(Printer.props(), "printerActor")
        val howdyGreeter = system.actorOf(Greeter.props("Howdy", printerActor), "howdyGreeter")
        val helloGreeter = system.actorOf(Greeter.props("Hello", printerActor), "helloGreeter")
        val goodDayGreeter = system.actorOf(Greeter.props("Good day", printerActor), "goodDayGreeter")

        println(system.uptime())
        howdyGreeter.tell(Greeter.WhoToGreet("Akka"), ActorRef.noSender())
        howdyGreeter.tell(Greeter.Greet(), ActorRef.noSender())

        howdyGreeter.tell(Greeter.WhoToGreet("Lightbend"), ActorRef.noSender())
        howdyGreeter.tell(Greeter.Greet(), ActorRef.noSender())

        helloGreeter.tell(Greeter.WhoToGreet("Java"), ActorRef.noSender())
        helloGreeter.tell(Greeter.Greet(), ActorRef.noSender())

        goodDayGreeter.tell(Greeter.WhoToGreet("Play"), ActorRef.noSender())
        goodDayGreeter.tell(Greeter.Greet(), ActorRef.noSender())

        println(">>> Press ENTER to exit <<<")
        readLine()

    } catch (ignored: Exception) {}
    finally {
        system.terminate()
    }
}