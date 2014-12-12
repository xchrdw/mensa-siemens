package de.chrdw.mensa_siemens.web;

import spark.ResponseTransformer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonTransformer implements ResponseTransformer {

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  @Override
  public String render(Object model) {
    return gson.toJson(model);
  }

}
