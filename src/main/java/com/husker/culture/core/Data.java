package com.husker.culture.core;

import com.husker.vkapi.database.City;
import com.husker.vkapi.enums.UserDataField;
import com.husker.vkapi.groups.Group;
import com.husker.vkapi.users.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Data {
    public static City city;
    public static ArrayList<String> groups = new ArrayList<>();

    public static HashMap<String, HashMap<UserDataField, String>> users_data;
    public static Users fur_users;
    public static Users city_users;
    public static HashMap<Group, List<String>> groups_users;
}
