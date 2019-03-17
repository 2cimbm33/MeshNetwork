package uni.cimbulka.network.simulator.gui.views

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import javafx.scene.control.Label
import tornadofx.View
import tornadofx.pane
import tornadofx.scrollpane

class SnapshotView : View() {
    private val lbl = Label("This is the snapshot view!")

    override val root = pane {
        scrollpane {
            add(lbl)
        }
    }

    fun display(node: JsonNode) {
        val text = ObjectMapper().apply {
            enable(SerializationFeature.INDENT_OUTPUT)
        }.writerWithDefaultPrettyPrinter().writeValueAsString(node)
        lbl.text = text
    }
}