package uni.cimbulka.network.simulator.bluetooth

import uni.cimbulka.network.simulator.physical.PhysicalLayer
import java.util.concurrent.locks.ReentrantLock


object AdapterPool {
    val lock = ReentrantLock()
    val adapters: MutableMap<String, BluetoothAdapter> = mutableMapOf()
        get() {
            lock.lock()
            try {
                return field
            } finally {
                lock.unlock()
            }
        }

    internal fun updateConnections(phy: PhysicalLayer) {
        lock.lock()
        try {
            adapters.forEach { id, adapter ->
                val connectionsToRemove = mutableListOf<String>()

                adapter.connections.forEach { otherId, _ ->
                    if (adapter.hasConnection(otherId) && !phy.inRange(id, otherId)) {
                        connectionsToRemove.add(otherId)
                    }
                }

                connectionsToRemove.forEach {
                    adapter.closeConnection(it)
                }
            }
        } catch (e: Exception) {
            throw e
        } finally {
            lock.unlock()
        }
    }
}