package uni.cimbulka.network.simulator.gui.views.dialogs

import javafx.geometry.Dimension2D
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextField
import tornadofx.*
import uni.cimbulka.network.simulator.mesh.RandomSimulationConfiguration

class RandomSimulationConfigDialog : Dialog<RandomSimulationConfiguration>() {
    init {
        title = "Simulation configuration"
        headerText = "Configure the simulation"

        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

        val noOfNodes = TextField()
        val noOfNodesToCreate = TextField()
        val width = TextField()
        val height = TextField()
        val duration = TextField()

        dialogPane.content = gridpane {
            add(Label("No. of nodes:"), 0, 0)
            add(noOfNodes, 1, 0)
            add(Label("No. of nodes to create:"), 0, 1)
            add(noOfNodesToCreate, 1, 1)
            add(Label("Width:"), 0, 2)
            add(width, 1, 2)
            add(Label("Height:"), 0, 3)
            add(height, 1, 3)
            add(Label("Duration:"), 0, 4)
            add(duration, 1, 4)
        }

        dialogPane.lookupButton(ButtonType.OK).apply {
            isDisable = true

            noOfNodes.textProperty().onChange { value ->
                isDisable = value?.let {
                    isDisable = it.toIntOrNull() == null ?: true
                    if (!isDisable) it.toInt() < 10
                    else isDisable
                } ?: true
            }

            noOfNodesToCreate.textProperty().onChange { value ->
                isDisable = value?.let {
                    isDisable = it.toIntOrNull() == null ?: true
                    if (!isDisable) it.toInt() < 0
                    else isDisable
                } ?: true
            }

            width.textProperty().onChange { value ->
                isDisable = value?.let {
                    isDisable = it.toDoubleOrNull() == null ?: true
                    if (!isDisable) it.toDouble() < 10.0
                    else isDisable
                } ?: true
            }

            height.textProperty().onChange { value ->
                isDisable = value?.let {
                    isDisable = it.toDoubleOrNull() == null ?: true
                    if (!isDisable) it.toDouble() < 10.0
                    else isDisable
                } ?: true
            }

            duration.textProperty().onChange { value ->
                isDisable = value?.let {
                    isDisable = it.toDoubleOrNull() == null ?: true
                    if (!isDisable) it.toDouble() < 10.0 * 1000
                    else isDisable
                } ?: true
            }
        }

        setResultConverter {
            if (it == ButtonType.OK) {
                RandomSimulationConfiguration(
                        noOfNodes.text.toInt(),
                        noOfNodesToCreate.text.toInt(),
                        Dimension2D(width.text.toDouble(), height.text.toDouble()),
                        duration.text.toDouble()
                )
            } else {
                null
            }
        }
    }
}