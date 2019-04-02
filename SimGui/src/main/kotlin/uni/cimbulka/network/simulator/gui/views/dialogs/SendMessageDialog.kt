package uni.cimbulka.network.simulator.gui.views.dialogs

import javafx.collections.FXCollections
import javafx.scene.control.*
import tornadofx.*
import uni.cimbulka.network.simulator.gui.models.SendMessageDialogResult

class SendMessageDialog(names: List<String>) : Dialog<SendMessageDialogResult>() {
    init {
        title = "Send Message dialog"
        headerText = "Send message"

        val sendButtonType = ButtonType("Send", ButtonBar.ButtonData.OK_DONE)
        dialogPane.buttonTypes.addAll(sendButtonType, ButtonType.CANCEL)

        val sender = ChoiceBox<String>().apply {
            items = FXCollections.observableList(names)
        }
        val recipient = ChoiceBox<String>().apply {
            items = FXCollections.observableList(names)
        }
        val message = textfield()

        dialogPane.content = gridpane {
            add(Label("Sender:"), 0, 0)
            add(sender, 0, 1)
            add(Label("Recipient:"), 1, 0)
            add(recipient, 1, 1)
            add(Label("Message:"), 2, 0)
            add(message, 2, 1)
        }

        dialogPane.lookupButton(sendButtonType).apply {
            isDisable = true

            message.textProperty().onChange {
                isDisable = it.isNullOrBlank()
            }
        }

        setResultConverter {
            if (it == sendButtonType) {
                SendMessageDialogResult(sender.value, recipient.value, message.text)
            } else {
                null
            }
        }
    }
}