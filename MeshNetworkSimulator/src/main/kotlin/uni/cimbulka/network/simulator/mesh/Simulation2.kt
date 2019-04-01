package uni.cimbulka.network.simulator.mesh

import org.neo4j.driver.v1.Driver
import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.core.events.ShutdownEvent

class Simulation2(driver: Driver) : BaseSimulation("Simulation2", driver) {
    override fun run() {
        val nodeA = getNode("Node A", Position(10.0, 10.0))
        val nodeB = getNode("Node B", Position(18.0, 10.0))
        val nodeC = getNode("Node C", Position(26.0, 10.0))
        val nodeD = getNode("Node D", Position(30.0, 16.0))
        val nodeE = getNode("Node E", Position(38.0, 16.0))
        val nodeF = getNode("Node F", Position(30.0, 4.0))

        nodeA.insertNode(0)
        nodeB.insertNode(5)
        nodeC.insertNode(10)
        nodeD.insertNode(15)
        nodeE.insertNode(20)
        nodeF.insertNode(25)

        insert(40 * 1000.0, "SendFirstMessage") {
            val controller = nodeA.controller ?: return@insert

            val packet = DataPacket.create(ApplicationData("Hello, D!"), controller, nodeD.device)
            controller.send(packet)
        }

        insert(50 * 1000.0, "SendSecondMessage") {
            val controller = nodeA.controller ?: return@insert

            val packet = DataPacket.create(ApplicationData("Hello, E and F!"), controller, nodeE.device, nodeF.device)
            controller.send(packet)
        }

        insert(ShutdownEvent(60 * 1000.0))
        start()
    }
}