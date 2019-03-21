package uni.cimbulka.network.simulator.bluetooth

import uni.cimbulka.network.simulator.common.Node

interface BluetoothAdapterCallbacks {
    fun discoveryFinished(neighbors: List<Node>)
    fun packetReceived(from: Node, packet: String)
    fun connectionCreated(node: Node)
    fun connectionClosed(node: Node)
}