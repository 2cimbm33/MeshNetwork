package uni.cimbulka.network.akka.actors

import akka.actor.AbstractActor
import akka.actor.ActorRef
import uni.cimbulka.network.akka.Constants
import uni.cimbulka.network.akka.common.Position

class PhysicalLayerActor : AbstractActor() {
    private val nodes = mutableMapOf<ActorRef, Position>()

    class AddNode(val node: ActorRef, val position: Position)
    class RemoveNode(val node: ActorRef)
    class MoveNode(val node: ActorRef, val dx: Double, val dy: Double)

    class GetNodePositionReq(val node: ActorRef)
    class GetNodePositionRes(val position: Position?)

    class InRangeReq(val first: ActorRef, val second: ActorRef)
    class InRangeRes(val result: Boolean)

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(AddNode::class.java) {
                    nodes[it.node] = it.position
                }
                .match(RemoveNode::class.java) {
                    if (nodes.keys.contains(it.node)) {
                        nodes.remove(it.node)
                        nodes.keys.remove(it.node)
                    }
                }
                .match(InRangeReq::class.java) {
                    val result = if (nodes.keys.contains(it.first) && nodes.keys.contains(it.second)) {
                        val fp = nodes[it.first]
                        val sp = nodes[it.second]

                        if (fp == null || sp == null) {
                            false
                        } else {
                            fp.distance(sp) <= Constants.Bluetooth.BLUETOOTH_RANGE
                        }
                    } else false

                    sender.tell(InRangeRes(result), self)
                }
                .match(GetNodePositionReq::class.java) {
                    sender.tell(GetNodePositionRes(nodes[it.node]), self)
                }
                .match(MoveNode::class.java) {
                    if (nodes.containsKey(it.node)) {
                        val current = nodes[it.node] ?: return@match
                        nodes.replace(it.node, Position(current.x + it.dx, current.y + it.dy))
                    }
                }
                .build()
    }
}