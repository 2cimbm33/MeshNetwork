package uni.cimbulka.network.simulator.gui.models

data class Simulation(val id: String, val name: String) {
    override fun toString(): String {
        return "$name - $id"
    }
}