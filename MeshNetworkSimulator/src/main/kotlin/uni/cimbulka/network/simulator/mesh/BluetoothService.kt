package uni.cimbulka.network.simulator.mesh

import uni.cimbulka.network.CommService
import uni.cimbulka.network.listeners.CommServiceCallbacks
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.packets.BasePacket
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapterCallbacks
import uni.cimbulka.network.simulator.common.Node
import uni.cimbulka.network.simulator.core.models.AbstractSimulator

class BluetoothService(val adapter: BluetoothAdapter, name: String, private val simulator: AbstractSimulator) : CommService(name) {
    private var shouldScan = false
    private var scanning = false

    override val neighbors: List<Device>
        get() {
            val result = mutableListOf<Device>()
            btNeighbors.forEach { result.add(it.device) }
            return result
        }

    override val isReady = true
    override val connectionString = adapter.node.id

    override var serviceCallbacks: CommServiceCallbacks? = null
    private val btNeighbors = mutableListOf<NetworkNode>()

    init {
        adapter.callbacks = object : BluetoothAdapterCallbacks {
            override fun discoveryFinished(neighbors: List<Node>) {
                val discovered = mutableListOf<Node>()
                val disconnected = btNeighbors.toMutableList()

                neighbors.forEach { node ->
                    val device = disconnected.firstOrNull { it.id == node.id }

                    if (device == null) {
                        discovered.add(node)
                    } else {
                        disconnected.remove(device)
                    }
                }

                val connected = mutableListOf<Node>()
                discovered.forEach {
                    if (adapter.validateNode(it.id)) {
                        connected.add(it)
                    }
                }

                btNeighbors.removeAll(disconnected)

                if (disconnected.isNotEmpty()) {
                    val input = mutableListOf<Device>()
                    disconnected.forEach { input.add(it.device) }

                    serviceCallbacks?.neighborDisconnected(*input.toTypedArray())
                }

                startService()

                scanning = false
            }

            override fun packetReceived(from: Node, packet: String) {
                if (from !in btNeighbors) {
                    val pac = BasePacket.fromJson(packet)

                    if (pac != null) {
                        pac.trace[pac.trace.size]?.let {
                            btNeighbors.add(NetworkNode(it, from.position))
                        }
                    }
                }

                serviceCallbacks?.onMessageReceived(packet)
            }

            override fun connectionCreated(node: Node) {
                sendHandshakeRequest(node.id)
                if (!scanning) scan()
            }

            override fun connectionClosed(node: Node) {
                val temp = btNeighbors.firstOrNull { it.id == node.id } ?: return

                btNeighbors.remove(temp)
                serviceCallbacks?.neighborDisconnected(temp.device)
                if (!scanning) scan()
            }
        }
    }

    override fun connect(connString: String): Boolean {
        return adapter.connect(connString) != null
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

    private fun sendHandshakeRequest(recipient: String) {
        val request = serviceCallbacks?.generateHandshakeRequest()
        adapter.sendPacket(recipient, request.toString())
    }

    override fun startService() {
        adapter.startService()
    }

    override fun stopService() {
        adapter.stopService()
    }

    private fun scan() {
        if (shouldScan && !scanning) {
            scanning = true
            adapter.startDiscovery();
        }
    }
}