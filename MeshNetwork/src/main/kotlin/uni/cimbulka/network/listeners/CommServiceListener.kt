package uni.cimbulka.network.listeners

import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.models.Device

internal class CommServiceListener(private val controller: NetworkController) : CommServiceCallbacks {
    private val discoveryListener = DiscoveryListener(controller)
    private val messageListener = MessageListener(controller)

    override fun onDiscoveryCompleted(devices: Array<Device>) {
        println("\nCommServiceListener:onDiscoveryComplete\n")
        discoveryListener.onDiscoveryCompleted(devices)
    }

    override fun onMessageReceived(packet: String) {
        println("\nCommServiceListener:onMessageReceived\n")
        messageListener.onMessageReceived(packet)
    }

    override fun onDeviceDisconnected(address: String) {
        println("\nCommServiceListener:onDeviceDisconnected\n")
        val session = controller.networkSession
        session.networkGraph.removeEdge(session.localDevice, session.neighbours[address] ?: return)
    }
}