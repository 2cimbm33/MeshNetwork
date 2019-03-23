package uni.cimbulka.network.simulator.mesh

import javafx.geometry.Point2D
import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.simulator.NetworkSimulator
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.physical.PhysicalLayer
import uni.cimbulka.network.simulator.physical.events.AddNodeEvent
import uni.cimbulka.network.simulator.physical.events.AddNodeEventArgs

abstract class BaseSimulation : NetworkSimulator(NetworkMonitor(PhysicalLayer())) {
    protected val phy = (monitor as NetworkMonitor).physicalLayer

    protected fun getNode(name: String, position: Point2D): NetworkNode {
        val controller = NetworkController(name)
        return NetworkNode(controller.localDevice, position).apply {
            this.controller = controller
        }
    }

    protected fun NetworkNode.insertNode(seconds: Int) {
        val time = seconds  * 1000.0
        val simulator = this@BaseSimulation
        simulator.insert(AddNodeEvent(time, AddNodeEventArgs(this, phy)))
        simulator.insert(time + 1, "Start${device.name}") { _ ->
            controller?.let {
                it.addCommService(BluetoothService(BluetoothAdapter(phy, this), it.localDevice.name, simulator))
                it.start()
            }
        }
    }
}