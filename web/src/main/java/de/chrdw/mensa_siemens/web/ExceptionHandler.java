package de.chrdw.mensa_siemens.web;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

public class ExceptionHandler implements spark.ExceptionHandler {

  private final Gson gson;
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public ExceptionHandler() {
    gson = new Gson();

  }

  @Override
  public void handle(Exception exception, Request request, Response response) {
    response.status(500);
    logger.error("Exception:", exception);
    Map<String, Object> model =
        ImmutableMap.of(
            "message", Objects.toString(exception.getMessage()),
            "stacktrace", Throwables.getStackTraceAsString(exception));
    response.body(gson.toJson(model));
    response.type("application/json");

  }


}
