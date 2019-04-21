package uni.cimbulka.network.simulator.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import javafx.geometry.Dimension2D
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import uni.cimbulka.network.simulator.mesh.BaseSimulationCallbacks
import uni.cimbulka.network.simulator.mesh.RandomSimulation
import uni.cimbulka.network.simulator.mesh.RandomSimulationConfiguration
import uni.cimbulka.network.simulator.mesh.reporting.Snapshot
import java.util.*

class Main : CliktCommand() {
    val probability: Int by option(help = "Probability of node creation").int().default(50)
    val nodes: Int by option(help = "Number of starting nodes").int().default(50)
    val width: Int by option(help = "Width of the simulation").int().default(100)
    val height: Int by option(help = "Height of the simulation").int().default(75)
    val duration: Int by option(help = "Duration of the simulation in milliseconds").int().default(45000)
    val collection: String by option(help = "Collection name in the database").default("simulation")
    val zone: Int by option(help = "The zone size of simulated network").int().default(5)

    private var numberOfEvents: Int = 0
    private var batchNumber: Int = 1
    private var batchStartedAt: Long = Date().time
    private var simulationStartedAt: Long = Date().time
    private var startSimTime: Double = 0.0

    override fun run() {
        val mongoClient = KMongo.createClient("mongodb://admin:nimda@cimbulka.win/admin?retryWrites=true")
                .coroutine
        val mongoCollection = mongoClient
                .getDatabase("mesh")
                .getCollection<Snapshot>(collection)

        val configuration = RandomSimulationConfiguration(
                probability,
                nodes,
                Dimension2D(width.toDouble(), height.toDouble()),
                duration.toDouble(),
                zone
        )

        val simulator = RandomSimulation(configuration, mongoCollection)
        val id = simulator.monitor.simId

        simulator.simulationCallbacks = object : BaseSimulationCallbacks {
            override fun eventExecuted(snapshot: Snapshot, time: Long) {
                numberOfEvents++

                if (numberOfEvents == 5000) {
                    printBatch(snapshot.time)

                    numberOfEvents = 0
                    batchNumber++
                    batchStartedAt = Date().time
                }
            }

            override fun simulationFinished(id: String) {
                printBatch(duration.toDouble(), last = true)
                val simulationTime = Date().time - simulationStartedAt
                println("Simulation $id finished in $simulationTime")
            }

        }

        println("Starting simulation $id")
        batchStartedAt = Date().time

        try {
            simulator.run()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mongoClient.close()
        }
    }

    fun printBatch(endSimTime: Double, last: Boolean = false) {
        val batchTime = Date().time - batchStartedAt
        val batchAvg = batchTime.toDouble() / numberOfEvents.toDouble()

        val str = if (last) "Last batch" else "Batch"
        println("[$batchNumber] $str executed in ${batchTime}ms [${batchAvg}ms average]; Simulation time: $startSimTime -> $endSimTime")
        startSimTime = endSimTime
    }
}

fun main(args: Array<String>) = Main().main(args)