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

class MeshSimulator : NetworkSimulator(NetworkMonitor(PhysicalLayer())) {
    override fun run() {
        val phy = (monitor as NetworkMonitor).physicalLayer

        val nodeA = getNode("Node A", Point2D(10.0, 10.0))
        val nodeB = getNode("Node B", Point2D(18.0, 10.0))
        val nodeC = getNode("Node C", Point2D(26.0, 10.0))

        insert(AddNodeEvent(0.0, AddNodeEventArgs(nodeA, phy)))
        insert(1.0, "StartNodeA") {
            nodeA.controller?.let { node ->
                node.addCommService(BluetoothService(BluetoothAdapter(phy, nodeA), node.localDevice.name))
                node.start()
            }
        }

        insert(AddNodeEvent(20.0 * 1000, AddNodeEventArgs(nodeB, phy)))
        insert((20.0 * 1000) + 1.0, "StartNodeB") {
            nodeB.controller?.let { node ->
                node.addCommService(BluetoothService(BluetoothAdapter(phy, nodeB), node.localDevice.name))
                node.start()
            }
        }

        insert(AddNodeEvent(50.0 * 1000, AddNodeEventArgs(nodeC, phy)))
        insert((50.0 * 1000) + 1.0, "StartNodeC") {
            nodeC.controller?.let { node ->
                node.addCommService(BluetoothService(BluetoothAdapter(phy, nodeC), node.localDevice.name))
                node.start()
            }
        }

        insert(90.0 * 1000, "SendPacketFromA-C") {
            nodeA.controller?.send(DataPacket(1, nodeA.device, nodeC.device, ApplicationData("Hello C!")))
        }

        insert(ShutdownEvent(100.0 * 1000))
        start()
    }

    private fun getNode(name: String, position: Point2D): NetworkNode {
        val controller = NetworkController(name, simulator = this)
        return NetworkNode(controller.localDevice, position).apply {
            this.controller = controller
        }
    }
}

fun main() {
    MeshSimulator().run()
}