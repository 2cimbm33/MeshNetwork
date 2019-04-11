package uni.cimbulka.network.simulator.mesh

import org.litote.kmongo.coroutine.CoroutineCollection
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.core.events.ShutdownEvent
import uni.cimbulka.network.simulator.mesh.events.SendRandomMessageEvent
import uni.cimbulka.network.simulator.mesh.events.SendRandomMessageEventArgs
import uni.cimbulka.network.simulator.mesh.reporting.Snapshot

class Simulation1(collection: CoroutineCollection<Snapshot>) : BaseSimulation(collection) {
    override fun run() {
        val nodeA = getNode("Node A", Position(10.0, 10.0))
        val nodeB = getNode("Node B", Position(18.0, 10.0))
        val nodeC = getNode("Node C", Position(26.0, 10.0))

        nodeA.insertNode(0)
        nodeB.insertNode(20)
        nodeC.insertNode(50)

        insert(SendRandomMessageEvent(90.0 * 1000, SendRandomMessageEventArgs(nodeA, nodeC, 3 * 1000 * 1000)))
        insert(ShutdownEvent(100.0 * 1000))
        start()
    }
}