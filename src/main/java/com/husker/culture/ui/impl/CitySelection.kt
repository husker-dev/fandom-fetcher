package com.husker.culture.ui.impl

import com.husker.culture.App
import com.husker.culture.AppTimer
import com.husker.culture.Resources
import com.husker.culture.core.Profile
import com.husker.culture.ui.core.Screen
import com.husker.minecraft.launcher.app.animation.NodeAnimation
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import java.util.*
import kotlin.system.exitProcess


class CitySelection: Screen() {

    companion object{
        private val countries = hashMapOf(
            "Россия" to 1,
            "Украина" to 2,
            "Беларусь" to 3,
            "Казахстан" to 4,
            "США" to 9,
            "Германия" to 65,
            "Латвия" to 12,
            "Литва" to 13,
            "Эстония" to 14,
            "Узбекистан" to 18
        )
    }

    private val country: ComboBox<String>
    private val resultPane: VBox
    private val searchField: TextField

    private lateinit var timer: Timer

    init{
        applyFX("city")
        country = findById("city_country") as ComboBox<String>
        resultPane = findById("city_result") as VBox
        searchField = findById("city_search") as TextField

        countries.forEach { (name, _) ->  country.items.add(name)}
        country.setOnAction { search(searchField.text) }

        searchField.textProperty().addListener {_, _, _ -> textChanged() }
    }

    private fun textChanged() {
        if (this::timer.isInitialized)
            timer.cancel()

        timer = AppTimer.create(500, 1000){
            search(searchField.text)
            timer.cancel()
        }
    }

    override fun onShow() {
        if (countries.containsValue(Profile.getCountry())) {
            for ((key, value) in countries.entries)
                if (value == Profile.getCountry() && country.value != key)
                    country.value = key
        } else {
            Alert(Alert.AlertType.ERROR, "Unsupported country!").showAndWait()
            exitProcess(0)
        }

        if (Profile.getCity() != null)
            searchField.text = Profile.getCity().name

    }

    private fun search(text: String){
        Thread{
            Platform.runLater { resultPane.children.clear() }
            val cities = Profile.api.DataBase.getCities(text, countries[country.value]!!, 8, true)

            for(city in cities){
                val cityPanel = Resources.fxml("city/city.fxml") as HBox
                var description = city.area
                if (city.region.isNotEmpty()) {
                    if (description.isNotEmpty()) description += ", "
                    description += city.region
                }

                NodeAnimation.showNode(cityPanel, NodeAnimation.Type.RISE, duration = 400)

                (findById(cityPanel, "city_title") as Label).text = city.name
                (findById(cityPanel, "city_description") as Label).text = description
                Platform.runLater { resultPane.children.add(cityPanel) }

                cityPanel.setOnMouseClicked {
                    Thread{
                        App.city = city
                        App.setScreen(GroupsScreen())
                    }.start()
                }
            }

        }.start()
    }
}