package uni.cimbulka.network.simulator.mesh.random

import uni.cimbulka.network.simulator.core.models.AbstractSimulator

data class RandomTicGeneratorConfiguration(
        val simulator: AbstractSimulator,
        val maxNumberOfNodes: Int
)