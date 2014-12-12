package de.chrdw.mensa_siemens.parser;

import java.time.LocalDate;
import java.util.List;

import de.chrdw.mensa_siemens.parser.model.Menu;

public class Main {

    public static void main(String[] args) {
        Parser parser = new Parser();
        System.out.println(parser.getRestaurants());
        List<Menu> menus = parser.getMenus(37, LocalDate.of(2014, 12, 6));
        for (Menu menu : menus) {
            System.out.println(menu);
        }
    }

}
