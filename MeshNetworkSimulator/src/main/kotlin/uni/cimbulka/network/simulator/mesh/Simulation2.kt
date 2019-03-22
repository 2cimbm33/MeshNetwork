package uni.cimbulka.network.simulator.mesh

import javafx.geometry.Point2D
import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.simulator.NetworkSimulator
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.core.events.ShutdownEvent
import uni.cimbulka.network.simulator.physical.PhysicalLayer
import uni.cimbulka.network.simulator.physical.events.AddNodeEvent
import uni.cimbulka.network.simulator.physical.events.AddNodeEventArgs

class Simulation2 : NetworkSimulator(NetworkMonitor(PhysicalLayer())) {
    private val phy = (monitor as NetworkMonitor).physicalLayer

    override fun run() {
        val nodeA = getNode("Node A", Point2D(10.0, 10.0))
        val nodeB = getNode("Node B", Point2D(18.0, 10.0))
        val nodeC = getNode("Node C", Point2D(26.0, 10.0))
        val nodeD = getNode("Node D", Point2D(30.0, 16.0))
        val nodeE = getNode("Node E", Point2D(38.0, 16.0))
        val nodeF = getNode("Node F", Point2D(30.0, 4.0))

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

    private fun getNode(name: String, position: Point2D): NetworkNode {
        val controller = NetworkController(name)
        return NetworkNode(controller.localDevice, position).apply {
            this.controller = controller
        }
    }

    private fun NetworkNode.insertNode(seconds: Int) {
        val time = seconds  * 1000.0
        val simulator = this@Simulation2
        simulator.insert(AddNodeEvent(time, AddNodeEventArgs(this, phy)))
        simulator.insert(time + 1, "Start${device.name}") { _ ->
            controller?.let {
                it.addCommService(BluetoothService(BluetoothAdapter(phy, this), it.localDevice.name, simulator))
                it.start()
            }
        }
    }
}