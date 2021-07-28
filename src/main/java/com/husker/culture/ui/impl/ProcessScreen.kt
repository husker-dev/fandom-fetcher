package com.husker.culture.ui.impl

import com.husker.culture.App
import com.husker.culture.AppTimer
import com.husker.culture.Resources
import com.husker.culture.core.Data
import com.husker.culture.core.FindingProcess
import com.husker.culture.ui.core.Screen
import com.husker.minecraft.launcher.app.animation.NodeAnimation
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.util.*
import javax.swing.JOptionPane
import kotlin.system.exitProcess


class ProcessScreen: Screen() {

    private val logo: ImageView
    private val progressText: Label
    private val progressBar: ProgressBar
    private val mainProgressBar: ProgressBar

    var logoImages = arrayListOf<Image>()

    init{
        applyFX("process")

        logo = findById("process_image") as ImageView
        progressText = findById("process_text") as Label
        progressBar = findById("process_bar") as ProgressBar
        mainProgressBar = findById("process_main_bar") as ProgressBar

        for(i in 1..15)
            logoImages.add(Resources.image("logo/p$i.png"))
    }

    override fun onShow() {
        NodeAnimation.showNode(logo, NodeAnimation.Type.RISE)

        var frame = 0
        AppTimer.create(0, 750){
            logo.image = logoImages[frame++ % logoImages.size]
        }

        Thread{startProcess()}.start()
    }

    private fun startProcess() {
        try {
            Data.city = App.city
            Data.groups = App.groups
            FindingProcess.start(App.groups.toTypedArray(), { current, max ->
                Platform.runLater {
                    mainProgressBar.progress = current.toDouble() / max
                }
            }, { text, current, max ->
                Platform.runLater {
                    progressText.text = text
                    progressBar.progress = current.toDouble() / max
                    progressText.text = text
                }
            }) { points ->
                Thread{ App.setScreen(ResultScreen(points)) }.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Alert(Alert.AlertType.ERROR, "Can't get info").showAndWait()
            exitProcess(0)
        }
    }
}