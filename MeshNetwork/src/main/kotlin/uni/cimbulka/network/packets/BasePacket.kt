package uni.cimbulka.network.packets

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import uni.cimbulka.network.NetworkConstants
import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.data.BaseData
import uni.cimbulka.network.data.EmptyData
import uni.cimbulka.network.models.Device
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "packetType")
@JsonSubTypes(
        Type(value = BroadcastPacket::class,        name = NetworkConstants.BROADCAST_PACKET_TYPE),
        Type(value = DataPacket::class,             name = NetworkConstants.DATA_PACKET_TYPE),
        Type(value = HandshakeRequest::class,       name = NetworkConstants.HANDSHAKE_REQUEST),
        Type(value = HandshakeResponse::class,      name = NetworkConstants.HANDSHAKE_RESPONSE),
        Type(value = RouteDiscoveryRequest::class,  name = NetworkConstants.ROUTE_DISCOVERY_REQUEST),
        Type(value = RouteDiscoveryResponse::class, name = NetworkConstants.ROUTE_DISCOVERY_RESPONSE)
)
abstract class BasePacket @JvmOverloads constructor(
        val id: Int,
        var source: Device,
        var data: BaseData = EmptyData(),
        val timestamp: Long) {

    var trace: MutableMap<Int, Device> = mutableMapOf()

    override fun toString(): String {
        return jacksonObjectMapper().apply {
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }.writeValueAsString(this)
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String) = try {
            jacksonObjectMapper().readValue<BasePacket>(json)
        } catch (e: UnrecognizedPropertyException) {
            null
        }
    }
}

fun main() {
    val sender = Device(UUID.randomUUID(), "PacketSender")
    val receiver = Device(UUID.randomUUID(), "Receiver")

    val dp = DataPacket(1, sender, receiver, ApplicationData("Hello World!")).apply {
        trace[0] = sender
    }

    val json = jacksonObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }.writerWithDefaultPrettyPrinter().writeValueAsString(dp)

    println(json)

    val packet = BasePacket.fromJson(json)

    if (packet != null) {
        println()
        println(packet::class.simpleName)
        println()
        println(packet is DataPacket)
        println(packet::class == DataPacket::class)
    }
}