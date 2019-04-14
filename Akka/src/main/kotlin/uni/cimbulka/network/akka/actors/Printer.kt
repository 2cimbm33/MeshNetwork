package uni.cimbulka.network.akka.actors

import akka.actor.AbstractActor
import akka.actor.Props

class Printer : AbstractActor() {
    class Greeting(val message: String)

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(Greeting::class.java) {
                    println(it.message)
                }
                .build()
    }

    companion object {
        fun props() = Props.create(Printer::class.java) { Printer() }
    }
}