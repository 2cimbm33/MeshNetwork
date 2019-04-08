package uni.cimbulka.network.listeners

import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.data.HandshakeData
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.packets.HandshakeRequest

internal class CommServiceListener(controller: NetworkController) : CommServiceCallbacks {
    private val session = controller.networkSession
    private val neighborListener = NeighborListener(controller.networkSession)
    private val messageListener = MessageListener(controller.networkSession)

    override fun neighborDisconnected(vararg neighbor: Device) {
        neighborListener.onNeighborDisconnected(*neighbor)
    }

    override fun onMessageReceived(packet: String) {
        //println("\nCommServiceListener:onMessageReceived\n")
        messageListener.onMessageReceived(packet)
    }

    override fun generateHandshakeRequest(): HandshakeRequest {
        return HandshakeRequest(
                session.incrementPacketCount(),
                session.localDevice,
                HandshakeData(session.networkGraph.export(), session.allDevices)
        ).apply {
            trace[1] = session.localDevice
        }
    }
}