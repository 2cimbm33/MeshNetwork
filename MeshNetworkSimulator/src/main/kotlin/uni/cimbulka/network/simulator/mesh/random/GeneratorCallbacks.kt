package uni.cimbulka.network.simulator.mesh.random

import uni.cimbulka.network.simulator.mesh.random.ticks.RandomTick

interface GeneratorCallbacks {
    fun generated(tick: RandomTick)
}