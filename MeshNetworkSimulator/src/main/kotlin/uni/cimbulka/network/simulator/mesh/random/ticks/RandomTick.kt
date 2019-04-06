package uni.cimbulka.network.simulator.mesh.random.ticks

import uni.cimbulka.network.simulator.mesh.NetworkNode

abstract class RandomTick(val node: NetworkNode, val type: Types) {

    enum class Types{
        CREATE_NODE,
        REMOVE_NODE,
        MOVE_NODE,
        SEND_MESSAGE
    }

}