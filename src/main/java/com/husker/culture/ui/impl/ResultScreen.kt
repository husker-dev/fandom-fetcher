package com.husker.culture.ui.impl

import com.husker.culture.Resources
import com.husker.culture.core.UserPoints
import com.husker.culture.ui.core.Screen
import com.husker.minecraft.launcher.app.animation.NodeAnimation
import com.husker.vkapi.enums.UserDataField
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import org.json.JSONObject
import java.awt.Desktop
import java.net.URI
import kotlin.math.min

class ResultScreen(private val points: ArrayList<UserPoints>): Screen() {

    private val resultPane: VBox
    private val backBtn: Button
    private val nextBtn: Button
    private val pageLabel: Label

    private val elementsOnPage = 6
    private var page = 0
    private var pages = points.size / elementsOnPage

    init{
        applyFX("result")

        println("Results: ${points.size}")

        resultPane = findById("result_list") as VBox
        backBtn = findById("result_back") as Button
        nextBtn = findById("result_next") as Button
        pageLabel = findById("result_page") as Label

        backBtn.isDisable = true
        if (page == pages) nextBtn.isDisable = true

        backBtn.setOnAction{
            page--
            nextBtn.isDisable = false
            if (page == 0) backBtn.isDisable = true
            Thread{updatePage()}.start()
        }

        nextBtn.setOnAction {
            page++
            backBtn.isDisable = false
            if (page == pages) nextBtn.isDisable = true
            Thread{updatePage()}.start()
        }
    }

    override fun onShow() {
        Thread{ updatePage() }.start()
    }

    private fun getPoints(from: Int): List<UserPoints?> {
        return points.subList(from, min(from + elementsOnPage, points.size))
    }

    private fun updatePage() {
        Platform.runLater {
            resultPane.children.clear()
            pageLabel.text = "${page+1} / ${pages+1}"
        }
        for (points in getPoints(page * elementsOnPage)) {
            val node = createUserPanel(points!!)
            Platform.runLater {
                resultPane.children.add(node)
            }
        }
    }

    private fun createUserPanel(points: UserPoints): Node{
        val node = Resources.fxml("result/user.fxml") as Parent

        (findById(node, "user_name") as Label).text = "${points.data[UserDataField.FIRST_NAME]} ${points.data[UserDataField.LAST_NAME]}"

        if (points.data.containsKey(UserDataField.CITY) && points.data[UserDataField.CITY] != null) {
            val cityObj = JSONObject(points.data[UserDataField.CITY])
            (findById(node, "user_description") as Label).text = cityObj.getString("title")
        }

        val pointsLabel = findById(node, "user_score") as Label
        pointsLabel.text = points.points.toString()

        val iconNode = findById(node, "user_icon") as ImageView
        val clip = Rectangle(iconNode.fitWidth, iconNode.fitHeight)
        clip.arcWidth = iconNode.fitWidth
        clip.arcHeight = iconNode.fitHeight
        iconNode.clip = clip

        Thread{
            val image = Image(points.data[UserDataField.PHOTO_200])
            Platform.runLater { iconNode.image = image }
        }.start()

        var color = Color.TRANSPARENT
        if(points.points > 2)
            color = Color.LIGHTGRAY
        if(points.points > 4)
            color = Color.LIGHTGREEN
        if(points.points > 10)
            color = Color.ORANGE
        if(points.points > 10)
            color = Color.RED
        pointsLabel.background = Background(BackgroundFill(color, CornerRadii(100.0, true), Insets.EMPTY))

        node.setOnMouseClicked {
            Desktop.getDesktop().browse(URI("https://vk.com/id" + points.user.id.toString() + "/"))
        }

        NodeAnimation.showNode(node, NodeAnimation.Type.RISE, duration = 400)

        return node
    }
}