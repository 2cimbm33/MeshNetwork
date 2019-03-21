package uni.cimbulka.network.simulator.bluetooth

import uni.cimbulka.network.simulator.NetworkSimulator
import uni.cimbulka.network.simulator.bluetooth.events.SendPacketEvent
import uni.cimbulka.network.simulator.bluetooth.events.SendPacketEventArgs
import uni.cimbulka.network.simulator.bluetooth.events.StartDiscoveryEvent
import uni.cimbulka.network.simulator.bluetooth.events.StartDiscoveryEventArgs
import uni.cimbulka.network.simulator.common.Node
import uni.cimbulka.network.simulator.physical.PhysicalLayer

class BluetoothAdapter(private val physicalLayer: PhysicalLayer, val node: Node) {

    var callbacks: BluetoothAdapterCallbacks? = null
    var discoverable: Boolean = false
        private set

    init {
        val id = node.id

        if (AdapterPool.adapters[id] == null) {
            AdapterPool.adapters[id] = this
        }
    }

    fun startDiscovery() {
        NetworkSimulator.simulator.insert(StartDiscoveryEvent(
                NetworkSimulator.simulator.time,
                StartDiscoveryEventArgs(
                        physicalLayer,
                        this)))
    }

    fun endDiscovery(neighbours: List<Node>) {
        callbacks?.discoveryFinished(neighbours)
    }

    fun sendPacket(recipient: String, data: String) {
        NetworkSimulator.simulator.insert(SendPacketEvent(
                NetworkSimulator.simulator.time,
                SendPacketEventArgs(BluetoothPacket(node.id, recipient, data))
        ))
    }

    fun receivedPacket(packet: BluetoothPacket) {
        val node = physicalLayer[packet.from] ?: return
        callbacks?.packetReceived(node, packet.data)
    }

    fun validateNode(id: String): Boolean {
        val adapter = AdapterPool.adapters[id] ?: return false
        return adapter.discoverable
    }

    fun startService() {
        discoverable = true
    }

    fun stopService() {
        discoverable = false
    }
}