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
    private var scanning = false

    override val neighbors: List<Device>
        get() = btNeighbors.toList()

    override val isReady = true

    override var serviceCallbacks: CommServiceCallbacks? = null
    private val btNeighbors = mutableListOf<Device>()

    init {
        adapter.callbacks = object : BluetoothAdapterCallbacks {
            override fun discoveryFinished(neighbors: List<Node>) {
                val discovered = mutableListOf<Device>()
                val disconnected = mutableListOf(*btNeighbors.toTypedArray())

                neighbors.forEach { node ->
                    val device = disconnected.firstOrNull { it.id.toString() == node.id }

                    if (device == null) {
                        discovered.add(node.extractDevice())
                    } else {
                        disconnected.remove(device)
                    }
                }

                val connected = mutableListOf<Device>()
                discovered.forEach {
                    if (adapter.validateNode(it.id.toString())) {
                        it.isInNetwork = true
                        connected.add(it)
                    }
                }

                disconnected.forEach {
                    btNeighbors.remove(it)
                }

                if (connected.isNotEmpty() || disconnected.isNotEmpty())
                    serviceCallbacks?.onNeighborsChanged(connected, disconnected)
                else
                    startService()

                scanning = false
            }

            override fun packetReceived(from: Node, packet: String) {
                serviceCallbacks?.onMessageReceived(packet)
            }

            override fun connectionCreated(node: Node) {
                val device = node.extractDevice()
                btNeighbors.add(device)
                if (!scanning) scan()
            }

            override fun connectionClosed(node: Node) {
                val device = node.extractDevice()
                btNeighbors.remove(device)
                if (!scanning) scan()
            }
        }
    }

    override fun startScanning() {
        shouldScan = true
        scan()
    }

    override fun stopScanning() {
        shouldScan = false
    }

    override fun sendPacket(packet: String, recipient: Device) {
        adapter.sendPacket(recipient.id.toString(), packet)
    }

    override fun startService() {
        adapter.startService()
    }

    override fun stopService() {
        adapter.stopService()
    }

    private fun scan() {
        if (shouldScan && !scanning) {
            simulator.insert(simulator.time, "CommServiceStartDiscovery-${adapter.node.id}") {
                scanning = true
                adapter.startDiscovery()
            }
        }
    }

    private fun Node.extractDevice(): Device {
        return when (this) {
            is NetworkNode -> device
            else -> Device(UUID.fromString(id))
        }
    }
}