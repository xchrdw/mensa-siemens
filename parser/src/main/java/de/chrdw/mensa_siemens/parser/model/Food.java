package de.chrdw.mensa_siemens.parser.model;

import lombok.Data;

import com.google.gson.annotations.Expose;

public @Data class Food {
    private final String id;
    @Expose
    private final String name;
    @Expose
    private final Float price;
}
