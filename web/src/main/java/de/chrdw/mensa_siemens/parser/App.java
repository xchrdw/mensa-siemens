package de.chrdw.mensa_siemens.parser;


import java.util.List;
import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Spark;

import com.google.common.base.Verify;
import com.google.common.collect.ImmutableMap;

/**
 * Hello world!
 */
public class App {

    private final Parser parser;

    public App() {
        parser = new Parser();
    }

    public static void main(String[] args) {
        App app = new App();

        System.out
                .println("try something like http://localhost:4567/index.html or /extract?uri=...");

        Spark.staticFileLocation("/static");
        Spark.get("/hello", (req, res) -> "Hello World");
        Spark.get("/mensas", app::getMensas, new JsonTransformer());
        Spark.exception(Exception.class, new ExceptionHandler());
    }


    public Map<String, Object> getMensas(Request req, Response res) {
        res.type("application/json");
        String q = getParam(req, "q");
        int limit = getIntParam(req, "count", 5);
        List<?> mensas = null;
        return ImmutableMap.of("search", q, "limit", limit, "mensas", mensas);
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

}
