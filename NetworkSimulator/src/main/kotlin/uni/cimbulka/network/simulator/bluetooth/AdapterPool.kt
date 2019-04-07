package uni.cimbulka.network.simulator.bluetooth

import uni.cimbulka.network.simulator.physical.PhysicalLayer


object AdapterPool {
    val adapters = mutableMapOf<String, BluetoothAdapter>()

    internal fun updateConnections(phy: PhysicalLayer) {
        synchronized(phy) {
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
        }
    }
}