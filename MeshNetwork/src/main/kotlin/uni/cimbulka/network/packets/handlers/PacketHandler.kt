package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.packets.*
import kotlin.reflect.KClass

internal interface PacketHandler<in T : BasePacket> {
    fun receive(packet: T, session: NetworkSession)
    fun send(packet: T, session: NetworkSession)

    companion object {
        fun <T : BasePacket> getHandler(type: KClass<T>): PacketHandler<*>? {
            return when (type) {
                BroadcastPacket::class -> BroadcastPacketHandler()
                DataPacket::class -> DataPacketHandler()
                HandshakeRequest::class -> HandshakeRequestHandler()
                HandshakeResponse::class -> HandshakeResponseHandler()
                RouteDiscoveryRequest::class -> RouteDiscoveryRequestHandler()
                RouteDiscoveryResponse::class -> RouteDiscoveryResponseHandler()

                else -> null
            }
        }
    }
}