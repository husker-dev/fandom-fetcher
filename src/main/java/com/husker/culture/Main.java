package com.husker.culture;

import com.husker.culture.core.Profile;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.vsync", "false");
        //Profile.api.setUserId(74005517);
        //Profile.api.setToken("0eb1355cc08d973610fe1ba0739541ab93947141c839391cd2a075196fe8775166c2a3362207e3d5aa85d");
        Application.launch(App.class, args);
    }
}
