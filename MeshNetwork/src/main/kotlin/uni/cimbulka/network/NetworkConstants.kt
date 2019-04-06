package uni.cimbulka.network

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

object NetworkConstants {
    const val SERVICE_UUID = "00000000-0000-1000-8000-b44ddf23279d"
    const val SERVICE_NAME = "MeshNetwork"
    const val LOCAL_DEVICE_NAME = "LocalDevice"

    const val BROADCAST_PACKET_TYPE = "broadcast-packet"
    const val DATA_PACKET_TYPE = "data-packet"
    const val HANDSHAKE_REQUEST = "handshake-request"
    const val HANDSHAKE_RESPONSE = "handshake-response"
    const val ROUTE_DISCOVERY_REQUEST = "route-discovery-request"
    const val ROUTE_DISCOVERY_RESPONSE = "route-discovery-response"

    const val UPDATE_DATA = "update-data"
    const val APPLICATION_DATA = "application-data"
    const val HANDSHAKE_RESPONSE_DATA = "handshake-response"
    const val EMPTY_DATA = "empty-data"

    const val ZONE_SIZE = 1

    fun generateUUID(address: String): String {
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.update(address.toByteArray())
        val digest = messageDigest.digest()
        return DatatypeConverter.printHexBinary(digest)
    }
}