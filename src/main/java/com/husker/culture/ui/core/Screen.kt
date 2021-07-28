package com.husker.culture.ui.core

import com.husker.culture.App
import com.husker.culture.Resources
import com.husker.culture.ui.impl.tools.FXUtils
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Pane

abstract class Screen {

    open lateinit var content: Pane

    fun findById(selector : String) : Node {
        return findById(content, selector)
    }

    fun findById(node: Parent, selector : String) : Node {
        return FXUtils.getChildByID(node, selector)
    }

    open fun applyFX(name : String){
        try {
            content = Resources.fxml("$name/content.fxml")
        }catch (e : Exception){}
        try {
            App.instance.contentPane.stylesheets.add(Resources.style("$name/content.css"))
        }catch (e : Exception){}
    }

    abstract fun onShow()
}