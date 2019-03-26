package uni.cimbulka.network.simulator.mesh.reporting

class Statistics(var node: String = "") {
    var discoveriesCompleted: Int = 0

    var totalPacketsSent: Int = 0
    var totalPacketsReceived: Int = 0

    var broadcastPacketSent: Int = 0
    var broadcastPacketReceived: Int = 0

    var dataPacketSent: Int = 0
    var dataPacketReceived: Int = 0

    var handshakeRequestsSent: Int = 0
    var handshakeRequestsReceived: Int = 0

    var handshakeResponsesSent: Int = 0
    var handshakeResponsesReceived: Int = 0

    var routeDiscoveryRequestsSent: Int = 0
    var routeDiscoveryRequestsReceived: Int = 0

    var routeDiscoveryResponsesSent: Int = 0
    var routeDiscoveryResponsesReceived: Int = 0
}