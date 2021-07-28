package com.husker.culture.ui.impl

import com.husker.culture.App
import com.husker.culture.Resources
import com.husker.culture.core.Profile
import com.husker.culture.ui.core.Screen
import com.husker.culture.ui.core.animation.easing.Elastic
import com.husker.culture.ui.impl.tools.AnimationUtils
import com.husker.minecraft.launcher.app.animation.NodeAnimation
import com.husker.vkapi.enums.GroupFields
import com.husker.vkapi.groups.Group
import com.husker.vkapi.groups.Groups
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import java.util.*

class GroupsScreen: Screen() {

    private val resultPane: VBox
    private val selectedPane: VBox
    private val startBtn: Button
    private val searchField: TextField

    private lateinit var timer: Timer

    private val selectedGroups = arrayListOf<String>()

    init{
        applyFX("groups")
        resultPane = findById("groups_results") as VBox
        selectedPane = findById("groups_selected") as VBox
        startBtn = findById("groups_start") as Button
        searchField = findById("groups_searchfield") as TextField

        searchField.textProperty().addListener { _, _, _ -> textChanged() }
        startBtn.setOnAction {
            Thread{
                App.selectedGroups = selectedGroups.toTypedArray()
                App.setScreen(ProcessScreen())
            }.start()
        }
    }

    override fun onShow() {
        searchField.textProperty().value = App.city.name
    }

    private fun textChanged() {
        if (this::timer.isInitialized)
            timer.cancel()

        timer = Timer()
        timer.schedule(object: TimerTask(){
            override fun run() {
                search(searchField.text)
            }
        }, 500)
    }

    private fun search(text: String) {
        Platform.runLater { resultPane.children.clear() }
        if(text.isEmpty())
            return

        if ("public" in text || "club" in text || "vk.com/" in text) {
            var id = text
            if (id.contains("vk.com/")) id = id.split("com/")[1]
            if (id.startsWith("public")) id = id.split("public")[1]
            if (id.startsWith("club")) id = id.split("club")[1]
            try {
                val group: Group = Profile.api.Groups.get(id)
                val data = group.getData(GroupFields.NAME, GroupFields.MEMBERS_COUNT, GroupFields.PHOTO_200)

                Platform.runLater { resultPane.children.add(createResultGroupPane(id, data)) }
            } catch (ignored: Exception) { }
        }
        val groups: Groups = Profile.api.Groups.search(text, 5)
        val data = groups.getData(GroupFields.NAME, GroupFields.MEMBERS_COUNT, GroupFields.PHOTO_200)

        Platform.runLater {
            for (id in groups.ids)
                resultPane.children.add(createResultGroupPane(id, data[id]!!))
        }
    }



    private fun createSelectedGroupPane(groupId: String, data: HashMap<GroupFields, String>): Node{
        val node = createGroupPane(data)
        node.setOnMouseClicked {
            selectedGroups.remove(groupId)
            selectedPane.children.remove(node)
        }

        Thread{
            NodeAnimation.showNode(node, NodeAnimation.Type.CENTER, easing = Elastic.In(), duration = 400).waitForEnd()
            AnimationUtils.setAnimatedMoving(node)
        }.start()

        return node
    }

    private fun createResultGroupPane(groupId: String, data: HashMap<GroupFields, String>): Node{
        val node = createGroupPane(data)
        node.setOnMouseClicked {
            if(groupId !in selectedGroups) {
                selectedGroups.add(groupId)
                val selectedPanel = createSelectedGroupPane(groupId, data)
                selectedPane.children.add(selectedPanel)
                selectedPane.requestLayout()
            }
        }
        NodeAnimation.showNode(node, NodeAnimation.Type.RISE, duration = 400)
        return node
    }

    private fun createGroupPane(data: HashMap<GroupFields, String>): Node {
        val node = Resources.fxml("groups/group.fxml") as Parent
        node.opacity = 0.0

        (findById(node, "group_name") as Label).text = data[GroupFields.NAME]
        (findById(node, "group_members") as Label).text = formatMembers(data[GroupFields.MEMBERS_COUNT]!!)
        val iconNode = findById(node, "group_icon") as ImageView

        val clip = Rectangle(iconNode.fitWidth, iconNode.fitHeight)
        clip.arcWidth = iconNode.fitWidth
        clip.arcHeight = iconNode.fitHeight
        iconNode.clip = clip

        Thread{
            val icon = Image(data[GroupFields.PHOTO_200])
            Platform.runLater { iconNode.image = icon }
        }.start()


        return node
    }

    private fun formatMembers(countStr: String): String {
        val chars = countStr.toCharArray()
        var subs = ""
        when (countStr.toInt() % 10) {
            0, 5, 6, 7, 8, 9 -> subs = "участников"
            1 -> subs = "участник"
            2, 3, 4 -> subs = "участника"
        }
        val builder = StringBuilder()
        for (i in countStr.length - 1 downTo 0) {
            if (builder.toString().replace(" ", "").length % 3 == 0) builder.append(" ")
            builder.append(chars[i])
        }
        return builder.reverse().append(" ").append(subs).toString().replace("\\s+".toRegex(), " ")
    }
}