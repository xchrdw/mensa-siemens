package de.chrdw.mensa_siemens.parser.model;

import lombok.Data;

import com.google.gson.annotations.Expose;

public @Data class Restaurant {
    @Expose
    public final int id;
    @Expose
    public final String name;
}
