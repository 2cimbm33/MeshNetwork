package uni.cimbulka.network.simulator.physical.events

import uni.cimbulka.network.simulator.common.Node
import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.physical.PhysicalLayer

data class RemoveNodeEventArgs(val node: Node, val physicalLayer: PhysicalLayer) : EventArgs()