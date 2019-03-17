package uni.cimbulka.network.simulator.mesh

import uni.cimbulka.network.CommService
import uni.cimbulka.network.listeners.CommServiceCallbacks
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapterCallbacks
import uni.cimbulka.network.simulator.common.Node
import java.util.*

class BluetoothService(private val adapter: BluetoothAdapter, name: String) : CommService(name) {
    override val neighbors: List<Device>
        get() = btNeighbors.toList()

    override val isReady = true
    override var serviceCallbacks: CommServiceCallbacks? = null

    private val btNeighbors = mutableListOf<Device>()

    init {
        adapter.callbacks = object : BluetoothAdapterCallbacks {
            override fun discoveryFinished(neighbors: List<Node>) {
                val devices = mutableListOf<Device>()
                neighbors.forEach { devices.add(it.extractDevice()) }

                btNeighbors.clear()
                btNeighbors.addAll(devices)

                serviceCallbacks?.onDiscoveryCompleted(devices.toTypedArray())
            }

            override fun packetReceived(packet: String) {
                serviceCallbacks?.onMessageReceived(packet)
            }
        }
    }

    override fun startDiscovery(inNetwork: Boolean): Boolean {
        adapter.startDiscovery()
        return true
    }

    override fun validateDevice(device: Device): Boolean {
        return adapter.validateNode(device.id.toString())
    }

    override fun sendPacket(packet: String, recipient: Device) {
        adapter.sendPacket(recipient.id.toString(), packet)
    }

    override fun startService() {}

    override fun stopService() {}

    private fun Node.extractDevice(): Device {
        return when (this) {
            is NetworkNode -> device
            else -> Device(UUID.fromString(id))
        }
    }
}