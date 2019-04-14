package uni.cimbulka.network.akka.actors

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.Props

class Greeter(private val message: String, private val printer: ActorRef) : AbstractActor() {
    private var greeting = "";

    class WhoToGreet(val who: String)
    class Greet

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(WhoToGreet::class.java) {
                    greeting = "$message, ${it.who}"
                }
                .match(Greet::class.java) {
                    printer.tell(Printer.Greeting(greeting), self)
                }
                .build()
    }

    companion object {
        fun props(message: String, printerActor: ActorRef): Props {
            return Props.create(Greeter::class.java) {
                Greeter(message, printerActor)
            }
        }
    }
}