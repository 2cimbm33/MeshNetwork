package uni.cimbulka.network.simulator.gui

import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.GraphDatabase
import java.io.Closeable

object Database : Closeable {
    val driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"))

    override fun close() {
        driver.closeAsync()
    }
}