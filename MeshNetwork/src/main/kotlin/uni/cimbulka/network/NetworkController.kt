package uni.cimbulka.network

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.coroutines.Job
import uni.cimbulka.network.listeners.CommServiceListener
import uni.cimbulka.network.listeners.NetworkCallbacks
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.packets.BroadcastPacket
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.packets.PacketSender
import java.util.*

class NetworkController(friendlyName: String) {

    private lateinit var mainJob: Job
    @JsonIgnore
    internal val networkSession = NetworkSession()

    var networkCallbacks: NetworkCallbacks?
        get() = networkSession.networkCallbacks
        set(value) { networkSession.networkCallbacks = value }

    val localDevice: Device
        get() = networkSession.localDevice

    init {
        networkSession.localDevice = Device(UUID.randomUUID(), friendlyName)
        networkSession.services.forEach {
            it.serviceCallbacks = CommServiceListener(this)
        }
        networkSession.networkGraph.addDevice(localDevice)
        networkSession.allDevices.add(localDevice)
    }

    fun getDevicesInNetwork(): List<Device> = networkSession.allDevices.filter { it != networkSession.localDevice }

    fun start() {
        startMainJob()
    }

    fun stop() {
        stopService()

        networkSession.mainJob = false
    }

    fun send(packet: BroadcastPacket) {
        PacketSender.send(packet, networkSession)
    }

    fun send(packet: DataPacket) {
        PacketSender.send(packet, networkSession)
    }

    fun addCommService(service: CommService) {
        service.serviceCallbacks = CommServiceListener(this)

        networkSession.localDevice.communications[service::class.java.canonicalName] = service.connectionString
        networkSession.services.add(service)
    }

    internal fun startServices() {
        networkSession.startServices()
    }

    internal fun stopService() {
        networkSession.services.forEach {
            it.stopScanning()
            it.stopService()
        }
    }

    private fun startMainJob() {
        networkSession.services.forEach { it.startScanning() }
        networkSession.mainJob = true
    }
}