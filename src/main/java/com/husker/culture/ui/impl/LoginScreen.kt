package com.husker.culture.ui.impl

import com.husker.culture.App
import com.husker.culture.Resources
import com.husker.culture.ui.core.Screen
import com.husker.minecraft.launcher.app.animation.NodeAnimation
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import com.husker.culture.core.Profile.*


class LoginScreen: Screen() {

    var logo: ImageView
    var loginButton: Button

    init{
        applyFX("login")

        logo = findById("logo") as ImageView
        logo.image = Resources.image("logo/p2.png")

        loginButton = findById("login_btn") as Button
        loginButton.setOnAction {
            api.openAuth()
        }

        api.addAuthListener {
            println("api_key: $it")
            App.setScreen(SubcultureSelection())
        }

        (findById("vk_logo") as ImageView).image = Resources.image("vk_logo.png")
    }

    override fun onShow() {

    }
}