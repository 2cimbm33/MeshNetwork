package uni.cimbulka.network.simulator.mesh

import org.litote.kmongo.coroutine.CoroutineCollection
import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.simulator.NetworkSimulator
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.mesh.events.StartNodeEvent
import uni.cimbulka.network.simulator.mesh.events.StartNodeEventArgs
import uni.cimbulka.network.simulator.mesh.reporting.Snapshot
import uni.cimbulka.network.simulator.physical.PhysicalLayer
import uni.cimbulka.network.simulator.physical.events.AddNodeEvent
import uni.cimbulka.network.simulator.physical.events.AddNodeEventArgs
import java.util.*

abstract class BaseSimulation(collection: CoroutineCollection<Snapshot>) :
        NetworkSimulator(NetworkMonitor(UUID.randomUUID().toString() ,PhysicalLayer(), collection)) {

    final override val monitor: NetworkMonitor = super.monitor as NetworkMonitor
    protected val phy: PhysicalLayer
        get() = monitor.physicalLayer

    var simulationCallbacks: BaseSimulationCallbacks?
        get() = monitor.callbacks
        set(value) { monitor.callbacks = value }

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
        simulator.insert(StartNodeEvent(time + 1, StartNodeEventArgs(this, phy)))
    }
}