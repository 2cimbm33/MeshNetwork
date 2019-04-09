package uni.cimbulka.network.simulator.mesh.random.ticks

abstract class RandomTick(val type: Types) {

    enum class Types{
        CREATE_NODE,
        MOVE_NODE,
        SEND_MESSAGE
    }

}