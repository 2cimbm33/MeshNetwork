package uni.cimbulka.network.simulator.mobility

import com.fasterxml.jackson.annotation.JsonIgnore
import uni.cimbulka.network.simulator.physical.PhysicalLayer

data class MobilityRule(val node: String, val speed: Double, val distance: Double, val direction: Direction, @JsonIgnore val physicalLayer: PhysicalLayer) {
    enum class Direction {
        UP, DOWN, LEFT, RIGHT, RANDOM
    }
}