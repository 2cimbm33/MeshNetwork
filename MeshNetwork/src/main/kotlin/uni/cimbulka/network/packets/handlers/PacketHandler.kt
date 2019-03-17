package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.packets.*

internal interface PacketHandler<in T : BasePacket> {
    fun receive(packet: T, session: NetworkSession)
    fun send(packet: T, session: NetworkSession)

    companion object {
        @Suppress("UNCHECKED_CAST")
        inline fun <reified T : BasePacket> getHandler(): PacketHandler<T>? {
            return when (T::class) {
                BroadcastPacket::class -> BroadcastPacketHandler()
                DataPacket::class -> DataPacketHandler()
                HandshakeRequest::class -> HandshakeRequestHandler()
                HandshakeResponse::class -> HandshakeResponseHandler()
                RouteDiscoveryRequest::class -> RouteDiscoveryRequestHandler()
                RouteDiscoveryResponse::class -> RouteDiscoveryResponseHandler()

                else -> null
            } as? PacketHandler<T>
        }
    }
}