package uni.cimbulka.network.simulator.gui

import tornadofx.*
import uni.cimbulka.network.simulator.gui.database.Database
import uni.cimbulka.network.simulator.gui.views.MainView

class Main : App(MainView::class) {
    override fun stop() {
        if (dbStarted)
            Database.close()

        super.stop()
    }

    companion object {
        var dbStarted = false
    }
}