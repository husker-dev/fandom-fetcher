package com.husker.culture

import com.husker.culture.ui.core.Screen
import com.husker.culture.ui.impl.*
import com.husker.minecraft.launcher.app.animation.NodeAnimation
import com.husker.vkapi.database.City
import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.stage.Stage

class App: Application() {

    companion object{
        @JvmStatic
        lateinit var instance: App

        fun setScreen(screen: Screen){
            if(instance.contentPane.children.size > 0)
                NodeAnimation.hideNode(instance.contentPane.children[0], NodeAnimation.Type.RISE, duration = 500).waitForEnd()
            Platform.runLater {
                screen.content.opacity = 0.0

                instance.contentPane.children.clear()
                instance.contentPane.children.add(screen.content)

                AnchorPane.setLeftAnchor(screen.content, 0.0)
                AnchorPane.setRightAnchor(screen.content, 0.0)
                AnchorPane.setTopAnchor(screen.content, 0.0)
                AnchorPane.setBottomAnchor(screen.content, 0.0)
                screen.onShow()

                NodeAnimation.showNode(screen.content, NodeAnimation.Type.RISE, duration = 500)
            }
        }

        val groups = arrayListOf<String>()
        lateinit var city: City
        lateinit var selectedGroups: Array<String>

    }

    var contentPane = AnchorPane()


    override fun start(stage: Stage?) {
        if(stage == null)
            throw UnsupportedOperationException("Stage can't be null")
        instance = this
        Resources.initializeFonts()

        stage.scene = Scene(contentPane, 100.0, 100.0)
        contentPane.background = Background(BackgroundFill(Color.WHITE, CornerRadii(0.0), Insets(0.0)))
        contentPane.stylesheets.add(Resources.style("base.css"))
        stage.width = 1000.0
        stage.height = 700.0
        stage.title = "Fandom Fetcher"
        stage.icons.add(Resources.image("logo/icon.png"))
        setScreen(WelcomeScreen())

        stage.show()
    }

}