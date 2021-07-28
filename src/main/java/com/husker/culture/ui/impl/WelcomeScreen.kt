package com.husker.culture.ui.impl

import com.husker.culture.App
import com.husker.culture.Resources
import com.husker.culture.ui.core.Screen
import com.husker.minecraft.launcher.app.animation.NodeAnimation
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.text.Font
import java.util.*

class WelcomeScreen: Screen() {

    var logoImages = arrayListOf<Image>()
    var logo: ImageView
    var textLabel: Label
    var logoTimer = Timer()

    var transitionEnd = false

    private val texts = arrayOf("meet friends", "be yourself")

    init{
        applyFX("welcome")
        logo = findById("logo") as ImageView
        textLabel = findById("welcome_text") as Label

        for(i in 1..15)
            logoImages.add(Resources.image("logo/p$i.png"))

        content.setOnMouseClicked{
            toNextScreen()
        }
    }

    override fun onShow() {
        NodeAnimation.showNode(logo, NodeAnimation.Type.RISE)

        logoTimer.schedule(object: TimerTask(){
            var frame = 0
            override fun run() {
                logo.image = logoImages[frame++ % logoImages.size]
            }
        }, 0, 750)

        Thread{
            Thread.sleep(1000)
            for(text in texts){
                Thread.sleep(500)

                for(i in text.indices){
                    Platform.runLater {
                        textLabel.text = text.substring(0, i + 1)
                    }
                    Thread.sleep(70)
                }
                Thread.sleep(1000)
                NodeAnimation.hideNode(textLabel, NodeAnimation.Type.RISE, duration = 750).waitForEnd()
                Platform.runLater {
                    textLabel.text = ""
                    textLabel.opacity = 1.0
                }
            }

            logoTimer.cancel()

            toNextScreen()
        }.start()
    }

    private fun toNextScreen(){
        if(transitionEnd)
            return
        transitionEnd = true
        Thread{App.setScreen(LoginScreen())}.start()
    }
}