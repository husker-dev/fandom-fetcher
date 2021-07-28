package com.husker.culture.core;

import com.husker.vkapi.database.City;
import com.husker.vkapi.enums.UserDataField;
import com.husker.vkapi.login.BrowserVkApi;
import com.husker.vkapi.users.User;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;

import static com.husker.vkapi.enums.UserDataField.*;

public class Profile {

    public static BrowserVkApi api = new BrowserVkApi(7842893);

    private static BufferedImage photo;
    private static String name;
    private static String lastName;
    private static City city;
    private static int country;

    static {
        api.addAuthListener(token -> initData());
    }

    public static void setPhoto(BufferedImage photo){
        Profile.photo = photo;
    }

    public static BufferedImage getPhoto(){
        return photo;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Profile.name = name;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastName) {
        Profile.lastName = lastName;
    }

    public static City getCity() {
        return city;
    }

    public static void setCity(City city) {
        Profile.city = city;
    }

    public static int getCountry() {
        return country;
    }

    public static void setCountry(int country) {
        Profile.country = country;
    }

    public static void initData(){
        if(name != null)
            return;

        try {
            User user = api.Users.get(api.getUserId() + "");
            HashMap<UserDataField, String> values = user.getData(PHOTO_200_ORIG, CITY, FIRST_NAME, LAST_NAME, COUNTRY);

            setPhoto(ImageIO.read(new URL(values.get(PHOTO_200_ORIG))));
            setName(values.get(FIRST_NAME));
            setLastName(values.get(LAST_NAME));

            try {
                JSONObject cityObj = new JSONObject(values.get(CITY));
                setCity(new City(cityObj.getLong("id"), cityObj.getString("title"), "", ""));
            }catch (Exception e){
                setCity(null);
            }
            try{
                JSONObject countryObj = new JSONObject(values.get(COUNTRY));
                setCountry(countryObj.getInt("id"));
            }catch (Exception ex){
                setCountry(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
