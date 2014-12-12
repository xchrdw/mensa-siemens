package de.chrdw.mensa_siemens.parser;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joox.JOOX;
import org.joox.Match;

import com.google.common.collect.ImmutableMap;

import de.chrdw.mensa_siemens.parser.model.Food;
import de.chrdw.mensa_siemens.parser.model.Restaurant;
import de.chrdw.mensa_siemens.parser.model.Menu;



/**
 */
public class Parser {

    private final QueryProvider queryProvider;
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    public Parser() {
        this(new QueryProvider());
    }

    Parser(QueryProvider queryProvider) {
        this.queryProvider = queryProvider;
    }

    public List<Restaurant> getRestaurants() {
        Match query = queryProvider.query("Restaurants", ImmutableMap.of());
        List<Restaurant> menus = query.find("Restaurants").map((restaurantContext) -> {
            Match restaurant = JOOX.$(restaurantContext);
            int id = Integer.parseInt(restaurant.child("RestaurantID").text());
            String name = restaurant.child("RestaurantName").text();
            return new Restaurant(id, name);
        });
        return menus;
    }

    public List<Menu> getMenus(int restaurantId, LocalDate date) {
        int windowSize = 10; // date calculation is fucked up
        LocalDate queryDate = date.minusDays(windowSize/2);
        Match query =
                queryProvider.query("Menus", ImmutableMap.of("RestaurantID", restaurantId, "MenuDate",
                        queryDate.format(formatter), "nextRecords", windowSize));
        List<List<Food>> foodLists = query.find("Menus").map((menu) -> {
            List<Food> foods = JOOX.$(menu).children().matchTag("Food[0-9]{2}_de").map((foodContext) -> {
                Match food = JOOX.$(foodContext);
                String id = food.tag().substring(4, 6);
                Match price = food.parent().find("Price" + id);
                return new Food(id, food.text().trim(), price.isEmpty() ? null : Float.parseFloat(price.text()));
            });
            return foods;
        });

        ArrayList<Menu> menus = new ArrayList<>();
        LocalDate curDay = queryDate;
        for (int i = 0; i < foodLists.size();) {
            if (curDay.getDayOfWeek() == DayOfWeek.SATURDAY || curDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
                menus.add(new Menu(Collections.<Food>emptyList(), curDay));
            } else {
                List<Food> list = foodLists.get(i++);
                menus.add(new Menu(list, curDay));
            }
            curDay = curDay.plusDays(1);
        }
        return menus;
    }

}
