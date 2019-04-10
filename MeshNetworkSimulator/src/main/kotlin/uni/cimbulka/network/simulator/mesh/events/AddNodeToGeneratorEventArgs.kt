package uni.cimbulka.network.simulator.mesh.events

import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.mesh.NetworkNode
import uni.cimbulka.network.simulator.mesh.random.RandomTickGenerator

data class AddNodeToGeneratorEventArgs(val node: NetworkNode, val generator: RandomTickGenerator) : EventArgs()