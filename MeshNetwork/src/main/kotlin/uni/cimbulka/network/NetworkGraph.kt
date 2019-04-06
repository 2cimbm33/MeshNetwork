package uni.cimbulka.network

import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import org.jgrapht.io.*
import uni.cimbulka.network.models.Device
import java.io.StringReader
import java.io.StringWriter
import java.util.*

internal data class NetworkGraph(private val session: NetworkSession,
                                 private val graph: SimpleGraph<Device, DefaultEdge> = SimpleGraph(DefaultEdge::class.java)) {
    var timestamp: Long = Date().time
        private set

    val devices: List<Device>
        get() = session.routingTable.keys.toList()

    val borderNodes: List<Device>
        get() {
            val result = mutableListOf<Device>()

            graph.vertexSet().forEach {
                if (getDistance(session.localDevice, it) == NetworkConstants.ZONE_SIZE) {
                    result.add(it)
                }
            }

            return result
        }

    fun export(): String {
        val exporter = GraphMLExporter<Device, DefaultEdge>().apply {
            setVertexIDProvider { it.id.toString() }
            setVertexAttributeProvider {
                mapOf(
                        "name" to DefaultAttribute.createAttribute(it.name),
                        "inNetwork" to DefaultAttribute.createAttribute(it.inNetwork)
                )
            }

            registerAttribute("name", GraphMLExporter.AttributeCategory.NODE, AttributeType.STRING)
            registerAttribute("inNetwork", GraphMLExporter.AttributeCategory.NODE, AttributeType.BOOLEAN)
        }
        val writer = StringWriter()
        exporter.exportGraph(graph, writer)
        val result = writer.toString()
        writer.close()
        return result
    }

    fun addDevice(device: Device): Boolean {
        timestamp = Date().time
        return if (!graph.containsVertex(device)) {
            val result = graph.addVertex(device)
            session.networkCallbacks?.onNetworkChanged(graph.vertexSet().toList())
            result
        }
        else
            false
    }

    fun removeDevice(device: Device): Boolean {
        timestamp = Date().time
        return if (graph.containsVertex(device)) {
            val result = graph.removeVertex(device)
            session.networkCallbacks?.onNetworkChanged(graph.vertexSet().toList())
            result
        }
        else
            false
    }

    fun addEdge(first: Device, second: Device): Boolean =
            if (!graph.containsEdge(first, second) || !graph.containsEdge(second, first)) {
                try {
                    if (!graph.containsVertex(first)) graph.addVertex(first)
                    if (!graph.containsVertex(second)) graph.addVertex(second)
                    graph.addEdge(first, second)
                    true
                } catch (ignored: Exception) {
                    false
                }
            }else  {
                false
            }

    fun edgesOf(device: Device): List<DefaultEdge> {
        return if (graph.containsVertex(device)) {
            graph.edgesOf(device).toList()
        } else {
            emptyList()
        }
    }

    fun removeEdge(first: Device, second: Device): Boolean = when {
        graph.containsEdge(first, second) -> {
            graph.removeEdge(first, second)
            timestamp = Date().time
            true
        }
        graph.containsEdge(second, first) -> {
            graph.removeEdge(second, first)
            timestamp = Date().time
            true
        }
        else -> false
    }

    fun calcRoutingTable(): RoutingTable {
        val routingMap = mutableMapOf<Device, Device>()
        val paths = DijkstraShortestPath<Device, DefaultEdge>(graph).getPaths(session.localDevice)

        for (device in graph.vertexSet()) {
            try {
                if (device != session.localDevice && graph.edgesOf(device).isNotEmpty()) {
                    routingMap[device] = paths.getPath(device).vertexList[1]
                }
            } catch (ignored: Exception) {}
        }

        return RoutingTable(routingMap)
    }

    fun merge(xml: String, mergeFrom: Device, session: NetworkSession) {
        val otherGraph = NetworkGraph.import(xml, session)

        for (device in otherGraph.devices) {
            val takeEdges = getDistance(session.localDevice, device) > getDistance(mergeFrom, device)
            addDevice(device)

            if (takeEdges) {
                for (edge in otherGraph.edgesOf(device)) {
                    val source = otherGraph.graph.getEdgeSource(edge)
                    val target = otherGraph.graph.getEdgeTarget(edge)

                    addEdge(source, target)
                }
            }
        }
    }

    private fun getDistance(first: Device, second: Device): Int {
        return try {
            val paths = DijkstraShortestPath<Device, DefaultEdge>(graph).getPaths(first)
            paths.getPath(second).vertexList.size - 1
        } catch (e: Exception) {
            -1
        }

    }

    companion object {
        @JvmStatic
        fun import(xml: String, session: NetworkSession): NetworkGraph {
            val graph = SimpleGraph<Device, DefaultEdge>(DefaultEdge::class.java)
            val vertexProvider = VertexProvider<Device> { id, attributes ->
                val name = attributes["name"]?.value ?: "Name not set"
                Device(UUID.fromString(id), name).apply {
                    inNetwork = attributes["inNetwork"]?.value?.toBoolean() ?: false
                }
            }
            val edgeProvider = EdgeProvider<Device, DefaultEdge> { _, _, _, _ -> DefaultEdge() }
            val reader = StringReader(xml)
            val importer = GraphMLImporter<Device, DefaultEdge>(vertexProvider, edgeProvider)

            importer.importGraph(graph, reader)
            reader.close()

            return NetworkGraph(session, graph)
        }
    }
}
