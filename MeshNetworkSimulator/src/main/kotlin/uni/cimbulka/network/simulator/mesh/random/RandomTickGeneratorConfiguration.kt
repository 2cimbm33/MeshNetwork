package uni.cimbulka.network.simulator.mesh.random

import javafx.geometry.Dimension2D
import uni.cimbulka.network.simulator.core.models.AbstractSimulator

data class RandomTickGeneratorConfiguration(
        val simulator: AbstractSimulator,
        val rule: Rule,
        val createProbability: Int,
        val dimension: Dimension2D = Dimension2D(100.0, 100.0)) {

    enum class Rule(val useEvents: Boolean) {
        CONTINUOUS(false),
        EVENT_DRIVEN(true)
    }
}