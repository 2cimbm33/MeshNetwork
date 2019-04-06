package uni.cimbulka.network

import uni.cimbulka.network.listeners.NetworkCallbacks
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.models.Update
import uni.cimbulka.network.packets.BasePacket
import uni.cimbulka.network.packets.DataPacket
import java.util.*

class NetworkSession {

    internal var networkGraph: NetworkGraph = NetworkGraph(this)

    val services: MutableList<CommService> = mutableListOf()
    var networkCallbacks: NetworkCallbacks? = null
    val longDistanceVectors: MutableMap<Device, Device> = mutableMapOf()
    val allDevices: MutableList<Device> = mutableListOf()
    lateinit var localDevice: Device
    private var packetCount = 0
    internal val pendingPackets = mutableMapOf<Device, MutableList<DataPacket>>()

    internal var processedUpdates = mutableMapOf<Long, Update>()

    val neighbours: Map<String, Device>
        get() {
            val result = mutableMapOf<String, Device>()

            for (service in services) {
                for (neighbor in service.neighbors) {
                    val id = neighbor.id.toString()
                    val device = result[id]

                    if (device == null) {
                        result[id] = neighbor
                    } else {
                        device.merge(neighbor)
                    }
                }
            }

            return result.toMap();
        }

    val processedPackets = mutableListOf<BasePacket>()
        get() {
            field.removeIf { Math.abs(Date().time - it.timestamp) > 5 * 60 * 1000 }
            return field
        }

    var isInNetwork = false
        set(value) {
            localDevice.inNetwork = value
            field = value
        }

    internal var routingTable = RoutingTable(emptyMap())
        get() = networkGraph.calcRoutingTable()
        private set

    fun incrementPacketCount(): Int {
        packetCount++
        return packetCount
    }

    fun startServices() {
        services.forEach { it.startService() }
    }

    //- Simulation stuff ----------------------------------------------------------------------------
    var mainJob = false
}