package uni.cimbulka.network.simulator.gui.database

import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.GraphDatabase
import uni.cimbulka.network.simulator.gui.Main

object Database {
    val driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"))

    init {
        Main.dbStarted = true
    }

    fun close() {
        driver.closeAsync()
    }
}