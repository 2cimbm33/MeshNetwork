package uni.cimbulka.network.simulator.gui.helpers

import javafx.util.StringConverter

class DoubleToStringConverter : StringConverter<Double>() {
    override fun toString(number: Double?): String {
        return number.toString()
    }

    override fun fromString(string: String?): Double {
        return string?.toDoubleOrNull() ?: Double.NaN
    }
}