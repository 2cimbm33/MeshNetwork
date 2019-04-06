package uni.cimbulka.network.data

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.models.Update

internal object DataProcessor {

    fun process(data: BaseData, session: NetworkSession) {
        when (data) {
            is ApplicationData ->  session.networkCallbacks?.onDataReceived(data)
            is UpdateData -> applyUpdates(data.updates, session)
        }
    }

    private fun applyUpdates(updates: List<Update>, session: NetworkSession) {
        for (update in updates) {
            val (nodes, action) = update
            val (first, second) = nodes

            when (action) {
                Update.CONNECTION_CREATED -> {
                    session.networkGraph.addDevice(first)
                    session.networkGraph.addDevice(second)
                    session.networkGraph.addEdge(first, second)
                }

                Update.CONNECTION_DELETED -> {
                    session.networkGraph.removeEdge(first, second)
                }
            }
        }
    }
}