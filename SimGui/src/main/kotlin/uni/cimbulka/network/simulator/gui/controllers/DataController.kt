package uni.cimbulka.network.simulator.gui.controllers

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.XYChart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.litote.kmongo.lte
import tornadofx.*
import uni.cimbulka.network.NetworkConstants
import uni.cimbulka.network.simulator.gui.database.Database
import uni.cimbulka.network.simulator.mesh.reporting.Snapshot
import kotlin.coroutines.EmptyCoroutineContext

class DataController : Controller() {
    val packetsData: ObservableList<XYChart.Data<String, Number>> = FXCollections.observableArrayList()
    val numberOfEvents: ObservableList<XYChart.Data<String, Number>> = FXCollections.observableArrayList()

    fun getPacketSpeed(collectionName: String) {
        val sentPackets = mutableMapOf<String, Number>()
        packetsData.clear()

        CoroutineScope(EmptyCoroutineContext).launch {

            val collection = Database.getCollection(collectionName)
            collection.find("{\"eventArgs.type\": 'data-packet'}").consumeEach {
                val type = it.eventArgs.get("type").asText()

                if (type == NetworkConstants.DATA_PACKET_TYPE) {
                    val id = it.eventArgs.get("id").asInt()
                    val sender = it.eventArgs.get("source").asText()
                    val recNode = it.eventArgs.get("recipients")
                    val recipients = mutableListOf<String>()
                    for (rec in recNode) { recipients.add(rec.asText()) }
                    val packetId = "$id::$sender"

                    when (it.eventName) {
                        "SendPacket" -> {
                            if (sender == it.nodeId) {
                                sentPackets[packetId] = it.time
                            }
                        }
                        "ReceivePacket" -> {
                            if (it.nodeId in recipients) {
                                sentPackets[packetId]?.toDouble()?.let { sendTime ->
                                    Platform.runLater {
                                        packetsData.add(XYChart.Data(packetId, (it.time - sendTime) / 1000))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getNumberOfEvents(collectionName: String) {
        var currentSecond = 0
        var total = 0
        numberOfEvents.clear()

        CoroutineScope(EmptyCoroutineContext).launch {
            Database.getCollection(collectionName).find(Snapshot::time lte 10000.0).consumeEach {
                Platform.runLater {
                    val sec = (it.time / 1000).toInt()

                    if (sec == currentSecond) {
                        total++
                    } else {
                        numberOfEvents.add(XYChart.Data(currentSecond.toString(), total))

                        currentSecond = sec
                        total = 1
                    }
                }
            }
        }
    }
}