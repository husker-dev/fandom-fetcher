package com.husker.culture;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class Resources {

    private static final String layoutPath = "/fx/layouts";
    private static final String fontPath = "/fx/fonts";
    private static final String stylesheetPath = "/fx/css";
    private static final String imagePath = "/images";
    private static final String svgPath = "/svg";

    public static void initializeFonts(){
        font("Bariol_Regular.otf");
        font("ChronicaPro-Black.ttf");
        font("ChronicaPro-Bold.ttf");
        font("ChronicaPro-ExtraBold.ttf");
        font("Allura-Regular.ttf");
    }

    public static URI uri(String path) throws URISyntaxException {
        return Resources.class.getResource(path).toURI();
    }

    public static Font font(String path){
        return Font.loadFont(Resources.class.getResourceAsStream(fontPath + "/" + path), 12);
    }

    public static String style(String path){
        return Resources.class.getResource(stylesheetPath + "/" + path).toExternalForm();
    }

    public static Image image(String path){
        return new Image(imagePath + "/" + path);
    }

    public static Image image(String path, int width, int height){
        return new Image(imagePath + "/" + path, width, height, false, false);
    }

    public static SVGPath svg(String path){
        var svg = new SVGPath();
        String content = read(svgPath + "/" + path);

        if(content.contains("<svg")) {
            ArrayList<String> paths = new ArrayList<>();
            for(String p : content.split("<path")){
                if(p.contains("fill=\"none\"") || !p.contains("d=\""))
                    continue;
                paths.add(p.split("d=\"")[1].split("\"")[0]);
            }
            svg.setContent(String.join(" ", paths));
        }else
            svg.setContent(content);
        return svg;
    }

    public static <T> T fxml(String path){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Resources.class.getResource(layoutPath + "/" + path));
        try {
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String read(String path){
        return new BufferedReader(new InputStreamReader(Resources.class.getResourceAsStream(path), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
    }
}
