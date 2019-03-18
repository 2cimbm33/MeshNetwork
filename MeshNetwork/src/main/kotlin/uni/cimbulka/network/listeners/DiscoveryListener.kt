package uni.cimbulka.network.listeners

import uni.cimbulka.network.NetworkController

internal class DiscoveryListener(private val controller: NetworkController) {
    /*private val session = controller.networkSession

    fun onDiscoveryCompleted(devices: Array<Device>) {
        println("\nDiscoveryListener:onDiscoveryCompleted\nInput: $devices")
        if (session.networkGraph.devices.isEmpty() ||
                (session.networkGraph.devices.size == 1 && session.networkGraph.devices.first() == session.localDevice)) {
            lookingForNetwork(devices)
        } else {
            inNetwork(devices)
        }

        println("\nSchedule next discovery")
        val simulator = session.simulator
        simulator.insert(StartDiscoveryEvent(simulator.time + 1000.0, StartDiscoveryEventArgs(session)))
    }

    private fun inNetwork(devices: Array<Device>) {
        println("Running inNetwork branch")
        println("Neighbours: ${session.neighbours}")
        val temp = session.neighbours.toMutableMap()
        val new = mutableListOf<Device>()

        devices.forEach {
            it.setIsInNetwork()

            if (it.isInNetwork) {
                if (it.id.toString() in temp) {
                    temp.remove(it.id.toString())
                } else {
                    new.add(it)
                }
            }
        }

        println("Creating update data")
        val data = UpdateData()

        if (temp.isNotEmpty()) {
            temp.forEach { k, v ->
                session.neighbours.remove(k)
                session.networkGraph.removeEdge(session.localDevice, v)
                data.updates.add(Update(session.localDevice, v, Update.CONNECTION_DELETED))
            }
        }

        if (new.isNotEmpty()) {
            new.forEach {
                session.neighbours[it.id.toString()] = it
                session.networkGraph.addDevice(it)
                session.networkGraph.addEdge(session.localDevice, it)
                data.updates.add(Update(session.localDevice, it, Update.CONNECTION_CREATED))
            }
        }

        println("Sending update: ${data.updates}")
        controller.updateNetwork(data)
    }

    private fun lookingForNetwork(devices: Array<Device>) {
        println("Running lookingForNetwork branch")
        devices.forEach {
            it.setIsInNetwork()

            if (it.isInNetwork) {
                session.neighbours[it.id.toString()] = it
            }
        }
        println("Neighbours: ${session.neighbours}")

        if (session.neighbours.isNotEmpty()) {
            println("Creating HandshakeRequest")
            val request = HandshakeRequest(session.incrementPacketCount(),
                    session.localDevice, session.neighbours.values.first())
            //println("Starting service")
            //controller.startService()
            println("Sending request: $request")
            PacketSender.send(request, session)
        } else {
            println("Setting up graph")
            session.networkGraph.addDevice(session.localDevice)
            println("Starting service")
            controller.startService()
        }
    }

    private fun Device.setIsInNetwork() {
        fun validate(): Boolean {
            session.services.forEach {
                if (it.validateDevice(this)) {
                    return true
                }
            }
            return false
        }

        println("setIsInNetwork: $this")
        println("Known devices: ${session.knownDevices}")
        if (id.toString() in session.knownDevices.keys) {
            isInNetwork = session.knownDevices[id.toString()]?.isInNetwork ?: false
        } else {
            val result = validate()
            kotlin.io.println("btService validateDevice result $result")
            isInNetwork = result
            session.knownDevices[id.toString()] = this
        }
        kotlin.io.println("Result: $isInNetwork")
    }
    */
}
