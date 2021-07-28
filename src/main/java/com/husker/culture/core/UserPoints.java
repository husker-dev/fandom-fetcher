package com.husker.culture.core;

import com.husker.vkapi.enums.UserDataField;
import com.husker.vkapi.groups.Group;
import com.husker.vkapi.groups.Groups;
import com.husker.vkapi.users.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.husker.vkapi.enums.UserDataField.*;

public class UserPoints {

    private int points = 0;
    private boolean intersect;
    private boolean sameCity = true;
    private User user;
    private HashMap<UserDataField, String> data;
    private final ArrayList<String> containingGroups = new ArrayList<>();

    public UserPoints(User user, HashMap<UserDataField, String> data, HashMap<Group, List<String>> groups_users, List<String> intersect){
        try {
            this.user = user;
            this.data = data;
            this.intersect = intersect.contains(user.getId());
            if (data.get(DEACTIVATED) != null) {
                points -= 100;
                return;
            }
            if (data.get(IS_CLOSED) != null && data.get(IS_CLOSED).equals("true"))
                points -= 2;
            //if(data.get(ONLINE) != null && data.get(ONLINE).equals("1"))
            //    points += 2;
            if (data.get(LAST_SEEN) != null) {
                long date = Long.parseLong(data.get(LAST_SEEN).split("\"time\":")[1].split(",")[0].split("}")[0]);
                long now = System.currentTimeMillis() / 1000;
                long month = 2592000;
                if (now - date > 10 * month)
                    points -= 10;
                else if (now - date > month)
                    points -= 2;
                else
                    points += 2;
            } else {
                points -= 7;
            }
            if (data.get(CITY) != null) {
                if (data.get(CITY).contains(Data.city.getName()))
                    points += 2;
                else {
                    sameCity = false;
                    points -= 30;
                }
            }
            if (data.get(HOME_TOWN) != null) {
                if (data.get(HOME_TOWN).equalsIgnoreCase(Data.city.getName()))
                    points += 1;
            }

            if (data.get(SCHOOLS) != null) {
                if (data.get(SCHOOLS).replaceAll("\\s+", " ").contains("city\":" + Data.city.getId() + ","))
                    points += 2;
            }

            for (Map.Entry<Group, List<String>> entry : groups_users.entrySet()) {
                if (entry.getValue().contains(user.getId())) {
                    points++;
                    containingGroups.add(entry.getKey().getId());
                }
            }
        }catch (Exception e){
            points = -999;
        }
    }

    public void deepScan(){
        int count = user.getGroupsCount();
        if(count > 500){
            if(count > 800)
                points = -5;
            else
                points -= getGroups() + 2;
        }else if(count > 0 && sameCity && getGroups() > 1){
            Groups groups = user.getGroups(30);

            boolean contain = false;
            for(int i = 0; i < Math.min(30, groups.getCount()); i++){
                if(containingGroups.contains(groups.getIds()[i])){
                    contain = true;
                    break;
                }
            }
            if(contain)
                points += 2;
            else
                points -= 5;
        }
    }

    public String toString(){
        return "(" + points + ", " + data.get(FIRST_NAME) + " " + data.get(LAST_NAME) + ")";
    }

    public int getGroups(){
        return containingGroups.size();
    }

    public boolean isIntersect(){
        return intersect;
    }

    public int getPoints(){
        return points;
    }

    public User getUser(){
        return user;
    }

    public HashMap<UserDataField, String> getData(){
        return data;
    }

    public int compareTo(UserPoints o) {
        return Integer.compare(points, o.getPoints());
    }
}
