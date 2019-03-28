package uni.cimbulka.network.simulator.bluetooth

import com.fasterxml.jackson.annotation.JsonIgnore
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

    @JsonIgnore
    val connections = mutableMapOf<String, BluetoothAdapter>()

    val connectionKeys: List<String>
        get() = connections.keys.toList()

    init {
        val id = node.id

        if (AdapterPool.adapters[id] == null) {
            AdapterPool.adapters[id] = this
        }
    }

    fun hasConnection(id: String): Boolean {
        return connections[id] != null
    }

    fun connect(connString: String): Node? {
        if (physicalLayer.inRange(node.id, connString)) {
            val adapter = AdapterPool.adapters[connString] ?: return null
            createConnection(adapter, false)
            adapter.createConnection(this, false)
            return adapter.node
        }

        return null
    }

    fun createConnection(adapter: BluetoothAdapter, invoke: Boolean = true) {
        connections[adapter.node.id] = adapter
        if (invoke) callbacks?.connectionCreated(adapter.node)
    }

    fun closeConnection(id: String) {
        val adapter = connections[id] ?: return

        connections.remove(id)
        callbacks?.connectionClosed(adapter.node)
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
                SendPacketEventArgs(BluetoothPacket(node.id, recipient, data), this)
        ))
    }

    fun receivedPacket(packet: BluetoothPacket) {
        val node = physicalLayer[packet.from] ?: return
        callbacks?.packetReceived(node, packet.data)
    }

    fun validateNode(id: String): Boolean {
        val adapter = AdapterPool.adapters[id] ?: return false
        val result = adapter.discoverable

        if (result) {
            createConnection(adapter)
        }

        return result
    }

    fun startService() {
        discoverable = true
    }

    fun stopService() {
        discoverable = false
    }
}