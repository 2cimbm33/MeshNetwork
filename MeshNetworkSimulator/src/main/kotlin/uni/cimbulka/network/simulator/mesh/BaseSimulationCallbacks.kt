package uni.cimbulka.network.simulator.mesh

import uni.cimbulka.network.simulator.mesh.reporting.Snapshot

interface BaseSimulationCallbacks {
    fun eventExecuted(snapshot: Snapshot, time: Long)
    fun simulationFinished(id: String)
}