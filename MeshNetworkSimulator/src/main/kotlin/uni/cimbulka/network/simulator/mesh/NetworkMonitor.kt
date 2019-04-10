package uni.cimbulka.network.simulator.mesh

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mongodb.client.model.WriteModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.insertOne
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
import uni.cimbulka.network.simulator.mesh.events.AddNodeToGeneratorEvent
import uni.cimbulka.network.simulator.mesh.events.SendRandomMessageEvent
import uni.cimbulka.network.simulator.mesh.events.StartNodeEvent
import uni.cimbulka.network.simulator.mesh.reporting.Snapshot
import uni.cimbulka.network.simulator.mobility.events.RunMobilityEvent
import uni.cimbulka.network.simulator.physical.PhysicalLayer
import uni.cimbulka.network.simulator.physical.events.AddNodeEvent
import uni.cimbulka.network.simulator.physical.events.MoveNodeEvent
import uni.cimbulka.network.simulator.physical.events.RemoveNodeEvent
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext

class NetworkMonitor(val simId: String,
                     physicalLayer: PhysicalLayer,
                     private val collection: CoroutineCollection<Snapshot>) : MonitorInterface {

    private val snapshots = mutableListOf<Snapshot>()
    internal var callbacks: BaseSimulationCallbacks? = null

    private var started = false
    private val mapper = jacksonObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }

    private var firstEvent: Long = 0
    private var numberOfEvents = 1
    private var previousEvent: Long = 0

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

        val pair: Pair<List<String>, List<String>> = if (node != null)
            getConnections(node) to getInRange(node)
        else
            Pair(emptyList(), emptyList())

        val snapshot = Snapshot(
                numberOfEvents, simId, time, name, getEventArgs(event), node?.id ?: "null",
                node?.position, pair.first, pair.second
        )

        snapshots.add(snapshot)
        callbacks?.eventExecuted(snapshot, timeDelta)

        if (snapshots.size == 5000) {
            //saveSnapshots(snapshots.map { it.copy() })
            snapshots.clear()
        }

        numberOfEvents++
        previousEvent = Date().time
    }

    override fun printRecords() {
        if (snapshots.isNotEmpty()) {
            //saveSnapshots(snapshots.map { it.copy() })
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

            collection.bulkWrite(requests)
        }
    }

    private fun getNode(event: EventInterface) = when (event) {
        is AddNodeEvent -> event.args.node
        is AddNodeToGeneratorEvent -> event.args.node
        is MoveNodeEvent -> event.args.node
        is RemoveNodeEvent -> event.args.node
        is RunMobilityEvent -> physicalLayer[event.args.rule.node]
        is StartDiscoveryEvent -> event.args.adapter.node
        is EndDiscoveryEvent -> event.args.adapter.node
        is SendRandomMessageEvent -> event.args.sender
        is SendPacketEvent -> event.args.adapter.node
        is ReceivePacketEvent -> event.args.adapter.node
        is StartNodeEvent -> event.args.node
        else -> null
    }

    private fun getConnections(node: Node): List<String> {
        val result = mutableListOf<String>()

        val adapter = AdapterPool.adapters[node.id] ?: return emptyList()
        adapter.connections.forEach { id, _ ->
            result.add(id)
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