package uni.cimbulka.network.simulator.mesh

import uni.cimbulka.network.simulator.common.Node
import uni.cimbulka.network.simulator.core.models.Event

data class ReportEvent(val event: Event<*>, val nodes: List<Node>)