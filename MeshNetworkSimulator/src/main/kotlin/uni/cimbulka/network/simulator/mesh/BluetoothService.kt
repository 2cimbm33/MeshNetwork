package uni.cimbulka.network.simulator.mesh

import uni.cimbulka.network.CommService
import uni.cimbulka.network.listeners.CommServiceCallbacks
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapterCallbacks
import uni.cimbulka.network.simulator.common.Node
import uni.cimbulka.network.simulator.core.Simulator
import java.util.*

class BluetoothService(private val adapter: BluetoothAdapter, name: String, private val simulator: Simulator) : CommService(name) {
    private var shouldScan = false

    override val neighbors: List<Device>
        get() = btNeighbors.toList()

    override val isReady = true

    override var serviceCallbacks: CommServiceCallbacks? = null
    private val btNeighbors = mutableListOf<Device>()

    init {
        adapter.callbacks = object : BluetoothAdapterCallbacks {
            override fun discoveryFinished(neighbors: List<Node>) {
                val connected = mutableListOf<Device>()
                val disconnected = mutableListOf(*btNeighbors.toTypedArray())

                neighbors.forEach { node ->
                    val device = disconnected.firstOrNull { it.id.toString() == node.id }

                    if (device == null) {
                        connected.add(node.extractDevice())
                    } else {
                        disconnected.remove(device)
                    }
                }

                btNeighbors.addAll(connected)
                btNeighbors.removeAll(disconnected)

                if (shouldScan) {
                    serviceCallbacks?.onNeighborsChanged(connected, disconnected)

                    simulator.insert(simulator.time + 1000.0, "CommServiceStartDiscovery-${adapter.node.id}") {
                        adapter.startDiscovery()
                    }
                }
            }

            override fun packetReceived(packet: String) {
                serviceCallbacks?.onMessageReceived(packet)
            }
        }
    }

    override fun startScanning() {
        shouldScan = true
        adapter.startDiscovery()
    }

    override fun stopScanning() {
        shouldScan = false
    }

    override fun sendPacket(packet: String, recipient: Device) {
        adapter.sendPacket(recipient.id.toString(), packet)
    }

    override fun startService() {}

    override fun stopService() {}

    private fun scan() {
        if (shouldScan) {
            adapter.startDiscovery()
        }
    }

    private fun Node.extractDevice(): Device {
        return when (this) {
            is NetworkNode -> device
            else -> Device(UUID.fromString(id))
        }
    }
}