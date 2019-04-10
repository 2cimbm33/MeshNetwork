package uni.cimbulka.network.simulator.gui.database

import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import uni.cimbulka.network.simulator.gui.Main
import uni.cimbulka.network.simulator.mesh.reporting.Snapshot

object Database {
    private val client = KMongo.createClient(
            "mongodb://admin:nimda@cimbulka.win/admin?retryWrites=true"
    ).coroutine

    private val database = client.getDatabase("mesh")

    init {
        Main.dbStarted = true
    }

    internal fun getCollection(collectionName: String) = database.getCollection<Snapshot>(collectionName)

    fun close() {
        client.close()
    }
}