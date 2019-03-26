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
            try {
                val result = mutableListOf<Device>()
                val paths = DijkstraShortestPath<Device, DefaultEdge>(graph).getPaths(session.localDevice)

                for (device in graph.vertexSet()) {
                    if (device != session.localDevice && graph.edgesOf(device).isNotEmpty()) {
                        if (paths.getPath(device).vertexList.size == NetworkConstants.ZONE_SIZE + 1) {
                            result.add(device)
                        }
                    }
                }

                return result
            } catch (ignored: Exception) {
                return emptyList()
            }
        }

    fun export(): String {
        val exporter = GraphMLExporter<Device, DefaultEdge>().apply {
            setVertexIDProvider { it.id.toString() }
            setVertexAttributeProvider {
                mapOf(
                        "name" to DefaultAttribute.createAttribute(it.name),
                        "isInNetwork" to DefaultAttribute.createAttribute(it.isInNetwork)
                )
            }

            registerAttribute("name", GraphMLExporter.AttributeCategory.NODE, AttributeType.STRING)
            registerAttribute("isInNetwork", GraphMLExporter.AttributeCategory.NODE, AttributeType.BOOLEAN)
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

    fun edgesOf(device: Device) = graph.edgesOf(device).toList()

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

    companion object {
        @JvmStatic
        fun import(csv: String, session: NetworkSession): NetworkGraph {
            val graph = SimpleGraph<Device, DefaultEdge>(DefaultEdge::class.java)
            val vertexProvider = VertexProvider<Device> { id, attributes ->
                val name = attributes["name"]?.value ?: "Name not set"
                Device(UUID.fromString(id), name).apply {
                    isInNetwork = attributes["isInNetwork"]?.value?.toBoolean() ?: false
                }
            }
            val edgeProvider = EdgeProvider<Device, DefaultEdge> { _, _, _, _ -> DefaultEdge() }
            val reader = StringReader(csv)
            val importer = GraphMLImporter<Device, DefaultEdge>(vertexProvider, edgeProvider)

            importer.importGraph(graph, reader)
            reader.close()

            return NetworkGraph(session, graph)
        }
    }
}
