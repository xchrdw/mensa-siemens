package de.chrdw.mensa_siemens.web;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Verify;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import de.chrdw.mensa_siemens.parser.Parser;
import de.chrdw.mensa_siemens.parser.model.Restaurant;
import de.chrdw.mensa_siemens.parser.model.Menu;
import lombok.SneakyThrows;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * Hello world!
 */
public class MensaApp {

    private final Parser parser;
    private final LoadingCache<MenuKey, List<Menu>> menuCache;
    private final LoadingCache<String, List<Restaurant>> restaurantCache;

    public MensaApp() {
        parser = new Parser();
        menuCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build(CacheLoader.from((key) -> {
                            return parser.getMenus(key.getMensaId(), key.getDay());
                        }
                ));
        restaurantCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build(CacheLoader.from((key) -> {
                            return parser.getRestaurants();
                        }
                ));
    }

    public static void main(String[] args) {
        MensaApp app = new MensaApp();

        System.out.println("try something like http://localhost:4567/index.html or /menus/37");

        Spark.staticFileLocation("/static");
        Spark.get("/hello", (req, res) -> "Hello World");
        Spark.get("/restaurants", app::getRestaurants, new JsonTransformer());
        Spark.get("/menus/:restaurantId", app::getMenus, new JsonTransformer());
        Spark.exception(Exception.class, new ExceptionHandler());
    }

    @SneakyThrows(ExecutionException.class)
    public Map<String, Object> getRestaurants(Request req, Response res) {
        res.type("application/json");
        List<Restaurant> restaurants = restaurantCache.get("");
        return ImmutableMap.of("restaurants", restaurants);
    }

    @SneakyThrows(ExecutionException.class)
    public Map<String, Object> getMenus(Request req, Response res)  {
        res.type("application/json");
        int id = Integer.parseInt(req.params("restaurantId"));
        LocalDate date = getLocalDateParam(req, "date", LocalDate.now());
        List<Menu> menus = menuCache.get(new MenuKey(id, date));
        Restaurant restaurant = restaurantCache.get("").stream().filter((r)->r.getId() == id).findFirst().orElse(new Restaurant(id, "unknown"));
        return ImmutableMap.of("restaurant", restaurant, "date", date.format(Parser.formatter), "menus", menus);
    }

    private String getParam(Request req, String name) {
        String p = req.queryParams(name);
        Verify.verifyNotNull(p, "Missing Argument: '%s'", name);
        return p;
    }

    private int getIntParam(Request req, String name, int defaultValue) {
        String p = req.queryParams(name);
        return p != null ? Integer.parseInt(p) : defaultValue;
    }

    private LocalDate getLocalDateParam(Request req, String name, LocalDate defaultValue) {
        String p = req.queryParams(name);
        return p != null ? LocalDate.parse(p, Parser.formatter) : defaultValue;
    }
}
