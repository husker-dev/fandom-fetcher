package com.husker.culture.ui.impl

import com.husker.culture.App
import com.husker.culture.Resources
import com.husker.culture.ui.core.Screen
import com.husker.culture.ui.impl.tools.AnimationUtils
import com.husker.culture.ui.impl.tools.FXUtils
import com.husker.culture.ui.impl.tools.ImageContentUtils
import com.husker.minecraft.launcher.app.animation.NodeAnimation
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

class SubcultureSelection: Screen() {

    private val cultures = arrayListOf(
        Culture("artist", "Художники"),
        Culture("programmer", "Программисты"),
        Culture("csgo", "Counter Strike"),
        Culture("dota", "Dota 2"),
        Culture("anime", "Аниме"),
        Culture("furry", "Фурри"),
        Culture("neet", "NEET"),
        Culture("umbrella", "Umbrella"),
    )

    init{
        applyFX("selection")
        (findById("scroll") as ScrollPane).isFitToWidth = true
    }

    override fun onShow() {
        Thread{
            Thread.sleep(1000)
            val icons = findById("icons") as Pane

            for(culture in cultures){
                Platform.runLater {
                    val node = Resources.fxml("selection/icon.fxml") as Pane
                    node.opacity = 0.0

                    node.setOnMouseClicked {
                        Thread{
                            App.groups.addAll(Resources.read("/groups/${culture.source}.txt").split("\n"))
                        }.start()
                        Thread{
                            App.setScreen(CitySelection())
                        }.start()

                    }

                    val image = findById(node, "icon_image") as ImageView
                    val text = findById(node, "icon_text") as Label

                    text.text = culture.label
                    image.image = Resources.image("cultures_icons/${culture.source}.png")
                    ImageContentUtils.scale(image)
                    icons.children.add(node)

                    NodeAnimation.showNode(node, NodeAnimation.Type.RISE)
                }

            }
        }.start()
    }

    data class Culture(val source: String, val label: String)
}