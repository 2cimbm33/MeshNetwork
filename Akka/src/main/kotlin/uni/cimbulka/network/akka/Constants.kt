package uni.cimbulka.network.akka

object Constants {
    object Bluetooth {
        const val BLUETOOTH_RANGE = 10.0
        const val TRANSMISSION_RATE = 375
        val DISCOVERY_DELAY_RANGE = (6.0 * 1000)..(11.0 * 1000)
        val TRANSPORT_DELAY_RANGE = 1.0..3.0
    }
}