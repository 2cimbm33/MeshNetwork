package uni.cimbulka.network.akka.actors

import akka.actor.AbstractActor

class Node : AbstractActor() {


    override fun createReceive(): Receive {
        return receiveBuilder()
                .build()
    }
}