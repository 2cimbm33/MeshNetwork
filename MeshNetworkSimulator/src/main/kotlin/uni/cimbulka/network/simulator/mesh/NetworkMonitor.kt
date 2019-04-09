package uni.cimbulka.network.simulator.mesh

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mongodb.client.model.WriteModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.insertOne
import org.litote.kmongo.reactivestreams.KMongo
import uni.cimbulka.network.NetworkConstants
import uni.cimbulka.network.packets.*
import uni.cimbulka.network.simulator.bluetooth.AdapterPool
import uni.cimbulka.network.simulator.bluetooth.events.EndDiscoveryEvent
import uni.cimbulka.network.simulator.bluetooth.events.ReceivePacketEvent
import uni.cimbulka.network.simulator.bluetooth.events.SendPacketEvent
import uni.cimbulka.network.simulator.bluetooth.events.StartDiscoveryEvent
import uni.cimbulka.network.simulator.common.Node
import uni.cimbulka.network.simulator.core.interfaces.EventInterface
import uni.cimbulka.network.simulator.core.interfaces.MonitorInterface
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.mesh.events.StartNodeEvent
import uni.cimbulka.network.simulator.mesh.reporting.Connection
import uni.cimbulka.network.simulator.mesh.reporting.Snapshot
import uni.cimbulka.network.simulator.mobility.events.RunMobilityEvent
import uni.cimbulka.network.simulator.physical.PhysicalLayer
import uni.cimbulka.network.simulator.physical.events.AddNodeEvent
import uni.cimbulka.network.simulator.physical.events.MoveNodeEvent
import uni.cimbulka.network.simulator.physical.events.RemoveNodeEvent
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("Duplicates")
class NetworkMonitor(private val simId: String,
                     physicalLayer: PhysicalLayer) : MonitorInterface {

    private val snapshots = mutableListOf<Snapshot>()
    internal var callbacks: BaseSimulationCallbacks? = null

    private var started = false
    private val lock = ReentrantLock()
    private val mapper = jacksonObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }

    private var firstEvent: Long = 0
    private var numberOfEvents = 1
    private var previousEvent: Long = 0

    private val client = KMongo.createClient(
            "mongodb+srv://user:WMRouMZdM439DGeg@centaurus-0wgaq.gcp.mongodb.net/mesh?retryWrites=true"
    ).coroutine
    private val col = client.getDatabase("mesh").getCollection<Snapshot>()

    lateinit var simulator: AbstractSimulator

    var physicalLayer: PhysicalLayer = physicalLayer
        set(value) {
            if (!started) field = value
        }

    override fun record(event: EventInterface) {
        val timeDelta = if (!started) {
            started = true
            firstEvent = Date().time
            0L
        } else {
            Date().time - previousEvent
        }
        val time = event.time
        val name = event.name

        val node = getNode(event)

        val pair: Pair<List<Connection>, List<String>> = if (node != null)
            getConnections(node) to getInRange(node)
        else
            emptyList<Connection>() to emptyList()

        val snapshot = Snapshot(
                numberOfEvents, simId, time, name, getEventArgs(event), node?.id ?: "null",
                node?.position, pair.first, pair.second
        )

        snapshots.add(snapshot)
        callbacks?.eventExecuted(snapshot, timeDelta)

        if (snapshots.size == 3000) {
            saveSnapshots(snapshots.map { it.copy() })
            snapshots.clear()
        }

        numberOfEvents++
        previousEvent = Date().time
    }

    override fun printRecords() {
        if (snapshots.isNotEmpty()) {
            saveSnapshots(snapshots.map { it.copy() })
            snapshots.clear()
        }
        println("Simulation finished in ${Date().time - firstEvent}ms")
        callbacks?.simulationFinished(simId)
    }

    private fun saveSnapshots(snapshots: List<Snapshot>) {
        CoroutineScope(EmptyCoroutineContext).launch {
            val requests = mutableListOf<WriteModel<Snapshot>>()
            snapshots.forEach {
                requests.add(insertOne(it))
            }

            col.bulkWrite(requests)
        }
    }

    private fun getNode(event: EventInterface) = when (event) {
        is AddNodeEvent -> event.args.node
        is MoveNodeEvent -> event.args.node
        is RemoveNodeEvent -> event.args.node
        is RunMobilityEvent -> physicalLayer[event.args.rule.node]
        is StartDiscoveryEvent -> event.args.adapter.node
        is EndDiscoveryEvent -> event.args.adapter.node
        is SendPacketEvent -> event.args.adapter.node
        is ReceivePacketEvent -> event.args.adapter.node
        is StartNodeEvent -> event.args.node
        else -> null
    }

    private fun getConnections(node: Node): List<Connection> {
        val result = mutableListOf<Connection>()

        val adapter = AdapterPool.adapters[node.id] ?: return emptyList()
        adapter.connections.forEach { id, _ ->
            result.add(Connection(node.id, id))
        }

        return result
    }

    private fun getInRange(node: Node): List<String> {
        val inRange = mutableListOf<String>()

        physicalLayer.keys.filter { it != node.id }.forEach {
            if (physicalLayer.inRange(node.id, it)) {
                inRange.add(it)
            }
        }

        return inRange
    }

    private fun getEventArgs(event: EventInterface): JsonNode {
        return mapper.createObjectNode().apply {
            when (event) {
                is MoveNodeEvent -> {
                    put("dx", event.args.dx)
                    put("dy", event.args.dy)
                }

                is SendPacketEvent -> {
                    putPacket(BasePacket.fromJson(event.args.packet.data))
                }

                is ReceivePacketEvent -> {
                    putPacket(BasePacket.fromJson(event.args.packet.data))
                }
            }
        }
    }

    private fun ObjectNode.putPacket(packet: BasePacket?) {
        if (packet == null) return

        put("id", packet.id)
        put("source", packet.source.id.toString())
        when (packet) {
            is BroadcastPacket -> put("type", NetworkConstants.BROADCAST_PACKET_TYPE)

            is DataPacket -> {
                put("type", NetworkConstants.DATA_PACKET_TYPE)
                putArray("recipients").apply {
                    packet.recipients.forEach { add(it.id.toString()) }
                }
            }

            is HandshakeRequest -> put("type", NetworkConstants.HANDSHAKE_REQUEST)

            is HandshakeResponse -> {
                put("type", NetworkConstants.HANDSHAKE_RESPONSE)
                put("recipient", packet.recipient.id.toString())
            }

            is RouteDiscoveryRequest -> {
                put("type", NetworkConstants.ROUTE_DISCOVERY_REQUEST)
                put("requester", packet.requester.id.toString())
                put("recipient", packet.recipient.id.toString())
                put("target", packet.target.id.toString())
            }

            is RouteDiscoveryResponse -> {
                put("type", NetworkConstants.ROUTE_DISCOVERY_RESPONSE)
                put("recipient", packet.recipient.id.toString())
            }
        }
    }
}