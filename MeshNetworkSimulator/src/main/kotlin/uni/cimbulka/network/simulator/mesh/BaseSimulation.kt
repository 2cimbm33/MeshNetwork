package uni.cimbulka.network.simulator.mesh

import javafx.geometry.Point2D
import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.simulator.NetworkSimulator
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.physical.PhysicalLayer
import uni.cimbulka.network.simulator.physical.events.AddNodeEvent
import uni.cimbulka.network.simulator.physical.events.AddNodeEventArgs
import java.util.*
import kotlin.reflect.KClass

abstract class BaseSimulation(type: String) :
        NetworkSimulator(NetworkMonitor(UUID.randomUUID().toString() ,PhysicalLayer(), type)) {
    protected val phy = (monitor as NetworkMonitor).physicalLayer

    protected fun getNode(name: String, position: Position): NetworkNode {
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