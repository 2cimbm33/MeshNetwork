package uni.cimbulka.network

import com.fasterxml.jackson.annotation.JsonIgnore
import uni.cimbulka.network.listeners.NetworkCallbacks
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.packets.BasePacket
import uni.cimbulka.network.simulator.core.Simulator

class NetworkSession {

    internal var networkGraph: NetworkGraph = NetworkGraph(this)
    @JsonIgnore
    lateinit var controller: NetworkController
    val neighbours = mutableMapOf<String, Device>()
    val processedPackets = mutableListOf<BasePacket>()
    val knownDevices = mutableMapOf<String, Device>()
    val services: MutableList<CommService> = mutableListOf()
    var networkCallbacks: NetworkCallbacks? = null
    val longDistanceVectors: MutableMap<Device, MutableCollection<Device>> = mutableMapOf()
    val allDevices: MutableList<Device> = mutableListOf()
    lateinit var localDevice: Device
    private var packetCount = 0

    var isInNetwork = false
        set(value) {
            localDevice.isInNetwork = value
            field = value
        }

    internal var routingTable = RoutingTable(emptyMap())
        get() {
            if (field.timestamp < networkGraph.timestamp) {
                field = networkGraph.calcRoutingTable()
            }
            return field
        }
        private set

    fun incrementPacketCount(): Int {
        packetCount++
        return packetCount
    }

    //- Simulation stuff ----------------------------------------------------------------------------
    lateinit var simulator: Simulator
    var mainJob = false
}