package com.husker.culture;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.vsync", "false");
        Application.launch(App.class, args);
    }
}
