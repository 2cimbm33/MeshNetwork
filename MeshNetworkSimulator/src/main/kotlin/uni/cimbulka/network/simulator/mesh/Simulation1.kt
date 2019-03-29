package uni.cimbulka.network.simulator.mesh

import javafx.geometry.Point2D
import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.simulator.NetworkSimulator
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.core.events.ShutdownEvent
import uni.cimbulka.network.simulator.physical.PhysicalLayer
import uni.cimbulka.network.simulator.physical.events.AddNodeEvent
import uni.cimbulka.network.simulator.physical.events.AddNodeEventArgs

class Simulation1 : BaseSimulation("Simulation1") {
    override fun run() {
        val nodeA = getNode("Node A", Position(10.0, 10.0))
        val nodeB = getNode("Node B", Position(18.0, 10.0))
        val nodeC = getNode("Node C", Position(26.0, 10.0))

        nodeA.insertNode(0)
        nodeB.insertNode(20)
        nodeC.insertNode(50)

        insert(90.0 * 1000, "SendPacketFromA-C") {
            nodeA.controller?.send(DataPacket(1, nodeA.device, nodeC.device, ApplicationData("Hello C!")))
        }

        insert(ShutdownEvent(100.0 * 1000))
        start()
    }
}

fun main() {
    Simulation1().run()
}