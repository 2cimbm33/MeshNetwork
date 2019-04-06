package uni.cimbulka.network.listeners

import uni.cimbulka.network.models.Device
import uni.cimbulka.network.packets.HandshakeRequest

interface CommServiceCallbacks {
    fun neighborDisconnected(vararg neighbor: Device)
    fun onMessageReceived(packet: String)
    fun generateHandshakeRequest(): HandshakeRequest
}
