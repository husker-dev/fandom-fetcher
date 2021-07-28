package com.husker.culture.core;

import com.husker.vkapi.enums.UserDataField;
import com.husker.vkapi.groups.Group;
import com.husker.vkapi.groups.Groups;
import com.husker.vkapi.users.User;
import com.husker.vkapi.users.Users;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static com.husker.culture.core.Data.*;
import static com.husker.culture.core.Data.city;
import static com.husker.culture.core.Data.city_users;
import static com.husker.culture.core.Data.fur_users;
import static com.husker.culture.core.Data.groups_users;
import static com.husker.culture.core.Data.users_data;
import static com.husker.culture.core.Profile.*;
import static com.husker.culture.core.Profile.api;
import static com.husker.vkapi.enums.UserDataField.*;
import static com.husker.vkapi.enums.UserDataField.CITY;
import static com.husker.vkapi.enums.UserDataField.FIRST_NAME;
import static com.husker.vkapi.enums.UserDataField.HOME_TOWN;
import static com.husker.vkapi.enums.UserDataField.IS_CLOSED;
import static com.husker.vkapi.enums.UserDataField.LAST_NAME;
import static com.husker.vkapi.enums.UserDataField.LAST_SEEN;
import static com.husker.vkapi.enums.UserDataField.PHOTO_200;
import static com.husker.vkapi.enums.UserDataField.SCHOOLS;

public class FindingProcess {

    public static void start(String[] groups, MainProcess main, SubProcess sub, Consumer<ArrayList<UserPoints>> onComplete){
        Groups fur_groups = api.Groups.get(groups).getNumberIds();
        Groups city_groups = api.Groups.get(Data.groups);
        if(groups_users == null)
            groups_users = new HashMap<>();

        main.apply(0, 6);

        // Получение участников
        if(fur_users == null)
            fur_users = getMembers("1", fur_groups, sub, groups_users);

        main.apply(1, 6);
        if(city_users == null)
            city_users = getMembers("2", city_groups, sub, null);
        main.apply(2, 6);

        System.out.println("Group's users: " + fur_users.getCount());

        // Получение их основных данных
        sub.apply("Получение данных участников...", 0, 100);
        if(users_data == null)
            users_data = fur_users.getData(percent -> sub.apply("Получение данных участников...", percent, 100), CITY, SCHOOLS, HOME_TOWN);
        main.apply(3, 6);

        // Поиск общих участников
        sub.apply("Поиск общик участников...", 0, 100);
        Users intersect = city_users.intersect(fur_users);
        main.apply(4, 6);

        // Поиск участников с таким же городом
        ArrayList<String> found = new ArrayList<>();
        for (int i = 0; i < fur_users.getCount(); i++) {
            sub.apply("Обработка данных...", i, fur_users.getCount());

            String id = fur_users.getIds()[i];
            HashMap<UserDataField, String> userData = users_data.get(id);
            try {
                if(userData.get(HOME_TOWN) != null && userData.get(HOME_TOWN).equalsIgnoreCase(city.getName())){
                    found.add(id);
                    continue;
                }
                if(userData.containsKey(CITY) && userData.get(CITY) != null && userData.get(CITY).contains(city.getName()) && userData.get(CITY).contains(city.getId() + "")){
                    found.add(id);
                    continue;
                }

                if (userData.containsKey(SCHOOLS))
                    if(userData.get(SCHOOLS).contains("\"city\":" + city.getId() + ","))
                        found.add(id);
            } catch (Exception ignored) { }
        }
        main.apply(5, 6);

        // Объединение
        sub.apply("Компановка всех участников...", 40, 100);
        Users all = new Users(api);
        all.combine(intersect);
        all.combine(found);
        sub.apply("Компановка всех участников...", 80, 100);
        all = all.getAlive();

        sub.apply("Получение данных пользователей...", 0, 100);
        HashMap<String, HashMap<UserDataField, String>> all_found_data = all.getData(percent -> {
            sub.apply("Получение данных пользователей... (1)", percent, 100);
        }, IS_CLOSED, LAST_SEEN, FIRST_NAME, LAST_NAME);

        all.getData(percent -> {
            sub.apply("Получение данных пользователей... (2)", percent, 120);
        }, PHOTO_200).forEach((key, value) -> all_found_data.get(key).putAll(value));
        all_found_data.forEach((key, value) -> {
            value.put(CITY, users_data.get(key).get(CITY));
            value.put(SCHOOLS, users_data.get(key).get(SCHOOLS));
            value.put(HOME_TOWN, users_data.get(key).get(HOME_TOWN));
        });
        /*
        all.getData(percent -> {
            sub.apply("Получение данных пользователей... (3)", percent, 100);
        }, CITY).forEach((key, value) -> all_found_data.get(key).putAll(value));
        all.getData(percent -> {
            sub.apply("Получение данных пользователей... (4)", percent, 100);
        }, SCHOOLS, HOME_TOWN).forEach((key, value) -> all_found_data.get(key).putAll(value));

         */

        main.apply(6, 6);
        ArrayList<UserPoints> points = new ArrayList<>();
        User[] allUsers = all.getUsers();
        for (int i = 0; i < allUsers.length; i++) {
            sub.apply("Расчёт приоритета...", i, allUsers.length);
            points.add(new UserPoints(allUsers[i], all_found_data.get(allUsers[i].getId()), groups_users, Arrays.asList(intersect.getIds())));
        }
        sub.apply("Сортировка...", 0, 100);
        points.sort((a1, a2) -> -Integer.compare(a1.getPoints(), a2.getPoints()));

        sub.apply("Дополнительный расчёт приоритета...", 0, 1);
        for (int i = 0; i < Math.min(50, points.size()); i++) {
            sub.apply("Дополнительный расчёт приоритета...", i, Math.min(50, points.size()));
            points.get(i).deepScan();
        }
        sub.apply("Сортировка...", 0, 100);
        points.sort((a1, a2) -> -Integer.compare(a1.getPoints(), a2.getPoints()));
        onComplete.accept(points);
    }

    public static Users getMembers(String tag, Groups groups, SubProcess sub, HashMap<Group, List<String>> groupMembers){
        Users found = new Users(api);
        for (int i = 0; i < groups.getCount(); i++) {
            try {
                Group group = groups.getGroups()[i];
                sub.apply("Получение участников... (" + tag + ")", i, groups.getCount());
                Users users = getMembersAnyway(group);
                if(groupMembers != null)
                    groupMembers.put(group, Arrays.asList(users.getIds()));
                found.combine(users);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return found;
    }

    private static Users getMembersAnyway(Group group){
        Users users;
        for(int i = 0; i < 5; i++){
            try {
                users = group.getMembers();
                return users;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        JOptionPane.showMessageDialog(null, "Невозможно получить доступ к VK API", "Ошибка", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
        return null;
    }

    public interface MainProcess{
        void apply(int progress, int maxProgress);
    }

    public interface SubProcess{
        void apply(String text, int progress, int maxProgress);
    }
}
