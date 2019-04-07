package uni.cimbulka.network.simulator.gui.controllers

import tornadofx.*
import uni.cimbulka.network.simulator.gui.events.RedrawCanvas
import uni.cimbulka.network.simulator.gui.models.Report

class SlideshowController : Controller() {
    private val mainController: MainController by inject()

    val report: Report?
        get() = mainController.report

    var playing: Boolean by property(false)
    fun playingProperty() = getProperty(SlideshowController::playing)

    fun play() {
        val delay = 100

        playing = true
        report?.events?.values?.forEach { snapshot ->
            runAsync {
                Thread.sleep(delay.toLong())
            } ui {
                fire(RedrawCanvas(snapshot.nodes, snapshot.connections))
            }
        }
        playing = false
    }
}